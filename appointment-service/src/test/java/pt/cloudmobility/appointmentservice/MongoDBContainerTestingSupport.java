package pt.cloudmobility.appointmentservice;

import com.github.silaev.mongodb.replicaset.MongoDbReplicaSet;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

public interface MongoDBContainerTestingSupport {

    MongoDbReplicaSet mongodb = MongoDbReplicaSet.builder().build();

    @DynamicPropertySource
    static void mongodbProperties(DynamicPropertyRegistry registry) {
        mongodb.start();
        registry.add("spring.data.mongodb.uri", mongodb::getReplicaSetUrl);
    }
}
