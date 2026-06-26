package com.club.badminton.service;

import com.club.badminton.dao.TrainingAttendanceDao;
import com.club.badminton.model.TrainingAttendance;
import com.club.badminton.model.User;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class AttendanceService {

    private final TrainingAttendanceDao attendanceDao;
    private final UserService userService;

    public AttendanceService(TrainingAttendanceDao attendanceDao, UserService userService) {
        this.attendanceDao = attendanceDao;
        this.userService = userService;
    }

    public List<LocalDate> findAllSessionDates() {
        return attendanceDao.findDistinctTrainingDates();
    }

    public List<TrainingAttendance> findByDate(LocalDate date) {
        return attendanceDao.findByDate(date.toString());
    }

    public void updateSingleStatus(int id, String status) {
        attendanceDao.updateStatus(id, status.toUpperCase());
    }

    public void cancelSessionGlobally(String dateString) {
        attendanceDao.cancelSessionByDate(dateString);
    }

    public void initializeSessionForDate(LocalDate date) {
        String dateString = date.toString();

        if (attendanceDao.existsByTrainingDate(dateString)) {
            return;
        }

        List<User> activeMembers = userService.findAllActiveMembers();
        if (activeMembers != null && !activeMembers.isEmpty()) {
            attendanceDao.initializeRosterForDate(dateString, activeMembers);
        }
    }

    public List<TrainingAttendance> findByUserId(int userId) {
        return attendanceDao.findByUserId(userId);
    }
}