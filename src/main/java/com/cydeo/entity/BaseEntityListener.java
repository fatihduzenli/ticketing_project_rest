package com.cydeo.entity;

import com.cydeo.entity.common.UserPrinciple;
import com.sun.security.auth.UserPrincipal;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

@Component
public class BaseEntityListener extends AuditingEntityListener {
// in the base entity, Whenever we update or create a project, we save this information to DB
    // Such as who did that and what time the update happened.By using BaseEntityListener we are assigning those values
    // Security will give us all the information
    @PrePersist //Marks this method to be called before an entity is persisted (inserted into the database).
    private void onPrePersist(BaseEntity baseEntity){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        baseEntity.setInsertDateTime(LocalDateTime.now());
        baseEntity.setLastUpdateDateTime(LocalDateTime.now());

        if(authentication != null && !authentication.getName().equals("anonymousUser")){
            Object principal = authentication.getPrincipal();
            baseEntity.setInsertUserId(((UserPrinciple) principal).getId());
            baseEntity.setLastUpdateUserId(((UserPrinciple) principal).getId());
        }
    }

    @PreUpdate // Marks this method to be called before an entity is updated.
    private void onPreUpdate(BaseEntity baseEntity){
//	•	Retrieves the current Authentication object from the security context, representing the user’s authentication state.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        baseEntity.setLastUpdateDateTime(LocalDateTime.now());

        if(authentication != null && !authentication.getName().equals("anonymousUser")){
            Object principal = authentication.getPrincipal();
            baseEntity.setLastUpdateUserId(((UserPrinciple) principal).getId());
        }
    }
}
