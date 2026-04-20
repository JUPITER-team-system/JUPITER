[1mdiff --git a/src/main/java/com/management/jupiter/impl/UserRepositoryImpl.java b/src/main/java/com/management/jupiter/impl/UserRepositoryImpl.java[m
[1mindex b41760c..cd8f15d 100644[m
[1m--- a/src/main/java/com/management/jupiter/impl/UserRepositoryImpl.java[m
[1m+++ b/src/main/java/com/management/jupiter/impl/UserRepositoryImpl.java[m
[36m@@ -7,7 +7,6 @@[m [mimport com.management.jupiter.models.Admin;[m
 import com.management.jupiter.persistance.DatabaseConnection;[m
 import com.management.jupiter.repository.UserRepository;[m
 import com.management.jupiter.models.enums.Role;[m
[31m-import com.management.jupiter.models.enums.TlType;[m
 [m
 import java.sql.*;[m
 import java.util.List;[m
[36m@@ -45,6 +44,11 @@[m [mpublic class UserRepositoryImpl implements UserRepository {[m
         return List.of();[m
     }[m
 [m
[32m+[m[32m    @Override[m
[32m+[m[32m    public Optional<User> findById(long id) {[m
[32m+[m[32m        return Optional.empty();[m
[32m+[m[32m    }[m
[32m+[m
     @Override[m
     public Optional<User> findById(String id) {[m
         String sql = "SELECT * FROM \"Cohorte\".user WHERE id = ?";[m
[36m@@ -70,6 +74,11 @@[m [mpublic class UserRepositoryImpl implements UserRepository {[m
 [m
     }[m
 [m
[32m+[m[32m    @Override[m
[32m+[m[32m    public void delete(long id) {[m
[32m+[m
[32m+[m[32m    }[m
[32m+[m
     @Override[m
     public void delete(String id) {[m
     }[m
[1mdiff --git a/src/main/java/com/management/jupiter/repository/UserRepository.java b/src/main/java/com/management/jupiter/repository/UserRepository.java[m
[1mindex 96010d2..ecfc969 100644[m
[1m--- a/src/main/java/com/management/jupiter/repository/UserRepository.java[m
[1m+++ b/src/main/java/com/management/jupiter/repository/UserRepository.java[m
[36m@@ -5,5 +5,10 @@[m [mimport java.util.Optional;[m
 [m
 public  interface UserRepository extends Repository<User> {[m
     public  Optional<User> findByEmail(String email);[m
[32m+[m
[32m+[m[32m    Optional<User> findById(String id);[m
[32m+[m
[32m+[m[32m    void delete(String id);[m
[32m+[m
     public User findByEmailorId(String emailOrId);[m
 }[m
\ No newline at end of file[m
