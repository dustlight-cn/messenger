package dustlight.messenger;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.CriteriaDefinition;
import org.springframework.data.mongodb.core.query.Query;

@SpringBootTest
class MessengerServiceApplicationTests {

    @Autowired
    ReactiveMongoOperations operations;

    @Test
    void contextLoads() {
        operations.save(new Data()).doOnError((e)->{
            System.out.println(e);
        }).block();
    }

    @Getter
    @Setter
    @Document
    public static class Data{

        @Id
        private ObjectId _id;
        private String a = "123";
        private Integer b = 1;
    }
}
