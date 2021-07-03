package com.lhind.tripapp.service.impl;

import com.lhind.tripapp.exception.EntityNotFoundException;
import com.lhind.tripapp.exception.RefreshTokenException;
import com.lhind.tripapp.model.RefreshToken;
import com.lhind.tripapp.model.User;
import com.lhind.tripapp.repository.RefreshTokenRepository;
import com.lhind.tripapp.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenServiceImpl implements com.lhind.tripapp.service.RefreshTokenService {

    @Value("${tripapp.jwtRefreshExpirationMs}")
    private Long refreshTokenDuration;

    private RefreshTokenRepository refreshTokenRepository;
    private UserRepository userRepository;
    private static final Logger logger = LogManager.getLogger("com.lhind.tripapp.service");


    @Autowired
    public RefreshTokenServiceImpl(RefreshTokenRepository refreshTokenRepository,
                                   UserRepository userRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Override
    public RefreshToken createRefreshToken(Long userId) {
        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setUser(userRepository.findById(userId).orElseThrow(
                () -> {
                    logger.error("Could not create refresh token because user with id: " + userId + " does not exist!");
                    return new EntityNotFoundException("Could not create refresh token because user doe not exist!");
                }
        ));
        refreshToken.setExpiryDate(Instant.now().plusMillis(this.refreshTokenDuration));
        refreshToken.setRefreshToken(UUID.randomUUID().toString());

        refreshToken = refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }

    @Override
    public RefreshToken verifyExpiration(RefreshToken token) {
        if(token.getExpiryDate().compareTo(Instant.now()) < 0) {
            logger.error("Refresh token has alredy expired: "+ token.getRefreshToken()+ " will be deleted!");
            refreshTokenRepository.delete(token);
            throw new RefreshTokenException(token.getRefreshToken(),"The refresh token has already expired!");
        }

        return token;
    }

    @Override
    public void deleteByUser(Long userId) {
        User tripOwner = userRepository.findById(userId).orElseThrow(
                () -> {
                    logger.error("Could not delete trip because user with id " + userId + " does not exist!");
                    return new EntityNotFoundException("Could not delete trip because user with this id does not exist!");
                }
        );
        refreshTokenRepository.deleteByUser(tripOwner);
    }
}
