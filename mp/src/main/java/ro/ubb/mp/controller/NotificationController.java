package ro.ubb.mp.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ro.ubb.mp.controller.dto.response.NotificationResponseDTO;
import ro.ubb.mp.controller.dto.response.PageResponseWrapperDTO;
import ro.ubb.mp.controller.dto.response.ResponseWrapperDTO;
import ro.ubb.mp.dao.model.User;
import ro.ubb.mp.service.notification.NotificationService;

import java.util.List;

@RestController
@RequestMapping
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);

    /**
     * obtinem paginat lista de notificari asociata unui user
     *
     * @param pageNr   int
     * @param pageSize int
     * @return 200 ok if we get the notifications, bad request otherwise
     * Postman example: localhost:8080/notifications/user?pageNr=0&pageSize=1
     * pastram momentan logger pt debug, va fi sters TODO
     */
    @GetMapping("/notifications/user")
    public ResponseEntity<PageResponseWrapperDTO<List<NotificationResponseDTO>>> getNotificationsByUser
    (
            @RequestParam(required = false, defaultValue = "") Integer pageNr,
            @RequestParam(required = false, defaultValue = "") Integer pageSize
    ) {
        logger.debug("inside getNotificationsByUser api for fetch user notification ");
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof User user) {

            return ResponseEntity.ok(notificationService.findByUserPaginated(user, pageNr, pageSize));
        }

        return ResponseEntity.badRequest().body(PageResponseWrapperDTO.<List<NotificationResponseDTO>>builder().errorMessage("bad authentication type").build());
    }

    /**
     * @param notificationId Long
     * @return 200 ok in case the patch was successfully, else bad request
     * Postman: localhost:8080/notifications/2/user/read  , unde 2 este id-ul notificarii de marcat, pt userul logat
     */
    @PatchMapping("/notifications/{id}/user/read")
    public ResponseEntity<ResponseWrapperDTO<String>> markNotificationAsReadForUser(@PathVariable("id") Long notificationId) {
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof User user) {
            notificationService.markAsRead(user, notificationId);

            return ResponseEntity.ok(ResponseWrapperDTO.<String>builder().value("Marked as Read").build());
        }

        return ResponseEntity.badRequest().body(ResponseWrapperDTO.<String>builder().errorMessage("bad authentication type").build());
    }
}
