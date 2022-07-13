package org.paasta.container.platform.common.api.users;

import org.paasta.container.platform.common.api.common.Constants;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * User Repository 인터페이스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2022.05.31
 */
@Repository
@Transactional
public interface UsersRepository extends JpaRepository<Users, Long>, JpaSpecificationExecutor<Users> {

    @Query(value = "SELECT DISTINCT service_account_name FROM cp_users", nativeQuery = true)
    List<String> getUsersNameList();

    @Query(value = "SELECT user_id FROM cp_users WHERE namespace = :namespace", nativeQuery = true)
    List<String> getUsersNameListByCpNamespaceOrderByCreatedDesc(@Param("namespace") String namespace);

    List<Users> findAllByCpNamespace(String namespace, Sort sort);

    List<Users> findAllByCpNamespaceAndUserIdContainingIgnoreCase(String namespace, String userId, Sort sort);


    @Query(value = "SELECT * FROM cp_users WHERE cluster_id = :clusterId AND namespace = :namespace  AND user_id = :userId limit 1 ;", nativeQuery = true)
    Users getOneUsersDetailByUserId(@Param("clusterId") String clusterId, @Param("namespace") String namespace, @Param("userId") String userId);

    @Query(value =
            "select * from (" +
                    "select id, user_id, user_auth_id, service_account_name, namespace, role_set_code, user_type, created" +
                    "       , (select case when count(user_id) > 0 " +
                    "                      then 'Y'" +
                    "                      else 'N' end " +
                    "from cp_users " +
                    "where namespace = :namespace" +
                    "             and user_id = cu.user_id) as display_yn" +
                    "  from cp_users cu" +
                    " where id in (select id" +
                    "                FROM cp_users cu" +
                    "               where namespace = :namespace" +
                    "               UNION all" +
                    "              SELECT max(id) AS id" +
                    "                FROM cp_users cu" +
                    "               WHERE NOT EXISTS (SELECT '1'" +
                    "                                   FROM cp_users a" +
                    "                                  WHERE namespace = :namespace" +
                    "                                    AND cu.user_id = a.user_id)" +
                    "               GROUP BY user_id)" +
                    "               ) cp where user_id in (select distinct user_id from cp_users where namespace = :defaultNamespace)" +
                    "               order by created desc;", nativeQuery = true)
    List<Object[]> findAllUsers(@Param("namespace") String namespace, @Param("defaultNamespace") String defaultNamespace);

    Users findByCpNamespaceAndUserId(String namespace, String userId);

    void deleteByCpNamespaceAndUserId(String namespace, String userId);

    @Query(value = "SELECT * FROM cp_users WHERE cluster_name = :cluster AND namespace = :namespace AND user_type ='" + Constants.AUTH_NAMESPACE_ADMIN + "'limit 1;", nativeQuery = true)
    Users findAllByClusterNameAndCpNamespace(@Param("cluster") String cluster, @Param("namespace") String namespace);

    @Query(value = "SELECT * FROM cp_users WHERE cluster_name = :cluster AND user_id = :userId AND namespace NOT IN (:defaultNamespace)", nativeQuery = true)
    List<Users> findAllByClusterNameAndUserId(@Param("cluster") String cluster, @Param("userId") String userId, @Param("defaultNamespace") String defaultNamespace);

    @Query(value = "SELECT * FROM cp_users WHERE cluster_name = :cluster AND user_id = :userId AND user_type ='" + Constants.AUTH_CLUSTER_ADMIN + "'limit 1;", nativeQuery = true)
    Users findByClusterNameAndUserIdAndUserType(@Param("cluster") String cluster, @Param("userId") String userId);


    @Query(value = "select * from cp_users where namespace = :namespace " +
            "and user_id not in (select distinct(user_id) from cp_users where namespace !=  :namespace) " +
            "and user_id not in (select distinct(user_id) from cp_users where user_type = :clusterAdmin) " +
            "and user_id like %:searchParam% " +
            "order by created desc", nativeQuery = true)
    List<Users> findByOnlyTempNamespaceUser(@Param("namespace") String namespace, @Param("searchParam") String searchParam, @Param("clusterAdmin") String clusterAdmin);


    List<Users> findAllByUserType(String userType);



    void deleteAllByUserIdAndUserType(String userId, String userType);

    List<Users> findAllByCpNamespaceAndUserIdAndUserType(String namespace, String userId, String userType);

    List<Users> findAllByCpNamespaceAndUserId(String namespace, String userId);


    @Query(value =
            "select A.id, A.user_id, A.user_auth_id, A.service_account_name, A.namespace, A.user_type, A.role_set_code, B.created" +
                    " from " +
                    " (select * from cp_users where namespace != :namespace and user_id not in (select distinct(user_id) from cp_users where user_type = :clusterAdmin) ) A ," +
                    " (select * from cp_users where namespace = :namespace and user_id not in (select distinct(user_id) from cp_users where user_type = :clusterAdmin) ) B" +
                    " where A.user_id = B.user_id" +
                    " and A.user_auth_id = B.user_auth_id" +
                    " and A.user_id like %:searchParam%" +
                    " order by B.created desc", nativeQuery = true)
    List<Object[]> findAllByUserMappingNamespaceAndRole(@Param("namespace") String namespace, @Param("searchParam") String searchParam, @Param("clusterAdmin") String clusterAdmin);


    @Query(value =
            "select  A.id, A.user_id, A.user_auth_id, A.service_account_name, A.namespace, A.user_type, A.role_set_code, A.service_account_secret, A.cluster_id, B.created" +
                    " from" +
                    " (select * from cp_users where namespace != :namespace and user_id = :userId) A ," +
                    " (select * From cp_users where namespace = :namespace and user_id = :userId and user_type = :userType) B" +
                    " where A.user_id = B.user_id order by A.namespace", nativeQuery = true)
    List<Object[]> findAllByUserMappingNamespaceAndRoleDetails(@Param("namespace") String namespace, @Param("userId") String userId, @Param("userType") String userType);


    @Query(value = "select * from cp_users where user_type = :userType and user_id like %:searchParam%", nativeQuery = true)
    List<Users> findAllByUserTypeAndLikeUserId(@Param("userType") String userType, @Param("searchParam") String searchParam);


    @Query(value =
            "select a.user_id, a.user_auth_id, if(isnull(b.user_id), 'N', 'Y') as is_nsadmin" +
                    " from" +
                    " (select user_id, user_auth_id from cp_users where namespace = :defaultNamespace group by user_id) a" +
                    " left join" +
                    " (select distinct user_id from cp_users where namespace = :searchNamespace and user_type = :userType) b" +
                    " on a.user_id =  b.user_id", nativeQuery = true)
    List<Object[]> findNamespaceAdminCheck(@Param("defaultNamespace") String defaultNamespace, @Param("searchNamespace") String searchNamespace,
                                           @Param("userType") String userType);


    void deleteAllByUserIdAndUserAuthIdAndCpNamespace(String userId, String userAuthId, String namespace);

    void deleteAllByCpNamespace(String namespace);

    List<Users> findAllByUserIdAndUserAuthIdOrderByCreatedDesc(String userId, String userAuthId);

    List<Users> findAllByUserIdOrderByCreatedDesc(String userId);



    List<Users> findAllByClusterIdAndCpNamespaceAndUserIdAndUserAuthId(String clusterId, String namespace, String userId, String userAuthId);


    @Query(value = "SELECT DISTINCT b.cluster_id, b.cluster_name, b.cluster_type, b.provider_type, a.user_type " +
            "FROM cp_users a, cp_clusters b " +
            "WHERE a.cluster_id = b.cluster_id " +
            "AND NOT (b.cluster_type = :clusterType AND a.namespace = :namespace ) " +
            "AND a.user_auth_id = :userAuthId " +
            "ORDER BY b.cluster_name; ", nativeQuery = true)
    List<Object[]> findClustersUsedByUser(@Param("clusterType") String clusterType, @Param("namespace") String namespace, @Param("userAuthId") String userAuthId);


    @Query(value = "SELECT * FROM cp_users "+
                   "WHERE cluster_id = :clusterId AND user_auth_id = :userAuthId AND namespace NOT IN (:defaultNamespace)", nativeQuery = true)
    List<Users> getUserMappingListByCluster(@Param("clusterId") String clusterId, @Param("userAuthId") String userAuthId,  @Param("defaultNamespace") String defaultNamespace);



    List<Users> findAllByClusterIdAndCpNamespaceAndUserAuthId(String clusterId, String namespace, String userAuthId);




    @Query(value = "delete from cp_users where user_id = :userId and user_auth_id != :userAuthId", nativeQuery = true)
    void deleteUsersWithUnequalAuthId(@Param("userId") String userId, @Param("userAuthId") String userAuthId);

    @Query(value = "SELECT* FROM cp_users WHERE user_id = :userId and user_auth_id != :userAuthId AND namespace != :defaultNamespace ;", nativeQuery = true)
    List<Users> getUsersListWithUnequalAuthId(@Param("userId") String userId, @Param("userAuthId") String userAuthId,  @Param("defaultNamespace") String defaultNamespace);

    @Query(value = "SELECT* FROM cp_users WHERE user_id = :userId and user_auth_id = :userAuthId AND namespace != :defaultNamespace ;", nativeQuery = true)
    List<Users> getUsersListWithAuthId(@Param("userId") String userId, @Param("userAuthId") String userAuthId,  @Param("defaultNamespace") String defaultNamespace);

    void deleteAllByUserType(String userType);

    void deleteAllByUserIdAndUserAuthId(String userId, String userAuthId);
}
