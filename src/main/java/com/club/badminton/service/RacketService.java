package com.club.badminton.service;

import com.club.badminton.dao.RacketDao;
import com.club.badminton.model.Racket;
import com.club.badminton.exception.BusinessValidationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RacketService {
    private final RacketDao racketDao;

    public RacketService(RacketDao racketDao) {
        this.racketDao = racketDao;
    }

    @Transactional
    public void addNewRacket(Racket racket) {
        racketDao.save(racket);
    }

    public List<Racket> getAllRackets() {
        return racketDao.findAll();
    }

    @Transactional
    public void assignPermanentRacket(int racketId, int memberId) {
        racketDao.updateStatus(racketId, "PERMANENTLY_BORROWED", memberId);
    }

    @Transactional
    public void updateRacketStatus(int racketId, String status) {
        if ("AVAILABLE".equals(status)) {
            racketDao.updateStatus(racketId, status, null);
        } else {
            racketDao.updateStatus(racketId, status, null);
        }
    }

    @Transactional
    public int autoReserveRacketForGuest() {
        // This calls the DAO to do the database work!
        Integer availableId = racketDao.findFirstAvailableRacket();
        if (availableId == null) {
            throw new BusinessValidationException("Cannot complete booking: No available rental rackets left in stock.");
        }
        racketDao.updateStatus(availableId, "TEMPORARILY_BORROWED", null);
        return availableId;
    }
}