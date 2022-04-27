package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.WeeklyScoreService;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.dto.WeeklyScoreDto;

import java.util.List;

@RestController
public class WeeklyScoreController {
    private static final Logger logger = LoggerFactory.getLogger(WeeklyScoreController.class);

    @Autowired
    private WeeklyScoreService weeklyScoreService;

    WeeklyScoreController(WeeklyScoreService weeklyScoreService) {
        this.weeklyScoreService = weeklyScoreService;
    }

    @GetMapping("/students/dashboards/{dashboardId}/weeklyScores")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#dashboardId, 'DASHBOARD.ACCESS')")
    public List<WeeklyScoreDto> getWeeklyScores(@PathVariable int dashboardId) {
        return this.weeklyScoreService.getWeeklyScores(dashboardId);
    }

    @DeleteMapping("/students/dashboards/{dashboardId}/weeklyScores/{weeklyScoreId}")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#dashboardId, 'DASHBOARD.ACCESS')")
    public void removeWeeklyScore(@PathVariable int dashboardId, @PathVariable int weeklyScoreId) {
        this.weeklyScoreService.removeWeeklyScore(weeklyScoreId);
    }

    @PutMapping("/students/dashboards/{dashboardId}/weeklyScores")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#dashboardId, 'DASHBOARD.ACCESS')")
    public void updateWeeklyScore(@PathVariable int dashboardId) {
        this.weeklyScoreService.updateWeeklyScore(dashboardId);
    }
}
