package mall;

import mall.config.kafka.KafkaProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PolicyHandler{
    @StreamListener(KafkaProcessor.INPUT)
    public void onStringEventListener(@Payload String eventString){

    }

    @Autowired
    OrderRepository orderRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverShiped_UpdateStatus(@Payload Shiped shiped){

        if(shiped.isMe()){
            Optional<Order> orderOptional = orderRepository.findById(shiped.getOrderId());
            Order order = orderOptional.get();
            order.setStatus(shiped.getStatus());

            orderRepository.save(order);

            System.out.println("##### listener UpdateStatus : " + shiped.toJson());
        }
    }


}
