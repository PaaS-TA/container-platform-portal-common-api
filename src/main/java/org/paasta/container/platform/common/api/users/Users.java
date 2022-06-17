package org.paasta.container.platform.common.api.users;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.paasta.container.platform.common.api.common.Constants;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * User Model 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2022.05.31
 */
@Entity
@Table(name = "cp_users")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Users {

    @Transient
    private String resultCode;

    @Transient
    private String resultMessage;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "cluster_id", nullable = false)
    private String clusterId;

    @Column(name = "user_id", nullable = false)
    @NotNull(message = "USER ID cannot be null")
    @NotEmpty(message = "USER ID is mandatory")
    private String userId;

    @Column(name = "user_auth_id", nullable = false)
    private String userAuthId;

    @Column(name = "namespace", nullable = false)
    private String cpNamespace;

    @Column(name = "user_type", nullable = false)
    private String userType;

    @Column(name = "role_set_code", nullable = false)
    private String roleSetCode;

    @Column(name = "service_account_name", nullable = false)
    private String serviceAccountName;

    @Column(name = "service_account_secret", nullable = false)
    private String saSecret;

    @Column(name = "service_account_token", nullable = false, length = 2000)
    private String saToken;

    @Column(name = "created", nullable = false, updatable = false)
    private String created;

    @Column(name = "last_modified", nullable = false)
    private String lastModified;


    @Transient
    private String isNsAdmin;
    @Transient
    private String clusterName;
    @Transient
    private String clusterType;


    @PrePersist
    void preInsert() {
        if (this.created == null) {
            this.created = LocalDateTime.now(ZoneId.of(Constants.STRING_TIME_ZONE_ID)).format(DateTimeFormatter.ofPattern(Constants.STRING_DATE_TYPE));
        }

        if (this.lastModified == null) {
            this.lastModified = LocalDateTime.now(ZoneId.of(Constants.STRING_TIME_ZONE_ID)).format(DateTimeFormatter.ofPattern(Constants.STRING_DATE_TYPE));
        }

    }


    public Users(){
    }

    public Users(String resultCode, String resultMessage) {
        this.resultCode = resultCode;
        this.resultMessage = resultMessage;
    }


    public Users(Object id, Object userId, Object userAuthId, Object serviceAccountName, Object cpNamespace, Object userType, Object roleSetCode, Object created) {
        this.id =  Integer.parseInt(String.valueOf(id));
        this.userId = (String) userId;
        this.userAuthId = (String) userAuthId;
        this.serviceAccountName = (String) serviceAccountName;
        this.cpNamespace = (String) cpNamespace;
        this.userType = (String) userType;
        this.roleSetCode = (String) roleSetCode;
        this.created = (String) created;
    }


   public Users(Object id, Object userId, Object userAuthId, Object serviceAccountName, Object cpNamespace, Object userType, Object roleSetCode, Object saSecret,
                 Object clusterId , Object created) {
        this.id =  Integer.parseInt(String.valueOf(id));
        this.userId = (String) userId;
        this.userAuthId = (String) userAuthId;
        this.serviceAccountName = (String) serviceAccountName;
        this.cpNamespace = (String) cpNamespace;
        this.userType = (String) userType;
        this.roleSetCode = (String) roleSetCode;
        this.saSecret = (String) saSecret;
        this.clusterId = (String) clusterId;
        this.created = (String) created;
    }


    public Users(Object userId, Object userAuthId, Object isNsAdmin) {
        this.userId = (String) userId;
        this.userAuthId = (String) userAuthId;
        this.isNsAdmin = (String) isNsAdmin;
    }


    public Users(Object clusterId, Object clusterName, Object clusterType, Object userType) {
        this.clusterId = (String) clusterId;
        this.clusterName = (String) clusterName;
        this.clusterType = (String) clusterType;
        this.userType = (String) userType;
    }



/*
    public Users(Object id, Object userId, Object userAuthId, Object serviceAccountName, Object cpNamespace, Object roleSetCode, Object userType, Object created) {
        this.id =  Integer.parseInt(String.valueOf(id));
        this.userId = (String) userId;
        this.userAuthId = (String) userAuthId;
        this.serviceAccountName = (String) serviceAccountName;
        this.cpNamespace = (String) cpNamespace;
        this.roleSetCode = (String) roleSetCode;
        this.userType = (String) userType;
        this.created = (String) created;
    }
*/

    @PreUpdate
    void preUpdate() {
        if (this.lastModified != null) {
            this.lastModified = LocalDateTime.now(ZoneId.of(Constants.STRING_TIME_ZONE_ID)).format(DateTimeFormatter.ofPattern(Constants.STRING_DATE_TYPE));
        }
    }


    @Override
    public String toString() {
        return "Users{" +
                "id=" + id +
                ", userAuthId='" + userAuthId + '\'' +
                ", userId='" + userId + '\'' +
                ", cpNamespace='" + cpNamespace + '\'' +
                ", roleSetCode='" + roleSetCode + '\'' +
                ", userType='" + userType + '\'' +
                '}'+ '\n' ;
    }
}
