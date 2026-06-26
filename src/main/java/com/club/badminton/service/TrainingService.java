package com.club.badminton.service;

import com.club.badminton.dao.TrainingAttendanceDao;
import com.club.badminton.model.TrainingAttendance;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class TrainingService {

    private final TrainingAttendanceDao trainingAttendanceDao;

    public TrainingService(TrainingAttendanceDao trainingAttendanceDao) {
        this.trainingAttendanceDao = trainingAttendanceDao;
    }

    public List<TrainingAttendance> findAllByDate(String date) {
        return trainingAttendanceDao.findByDate(date);
    }

    @Transactional
    public void markAsPresent(int attendanceId) {
        trainingAttendanceDao.updateStatus(attendanceId, "PRESENT");
    }

    @Transactional
    public void cancelTrainingSession(String date) {
        trainingAttendanceDao.cancelSessionByDate(date);
    }

}