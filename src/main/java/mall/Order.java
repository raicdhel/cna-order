package mall;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;

@Entity
@Table(name="Order_table")
public class Order {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private String productId;
    private Integer qty;
    private String status;

    @PostPersist
    public void onPostPersist(){
        Ordered ordered = new Ordered();
        BeanUtils.copyProperties(this, ordered);
        ordered.publishAfterCommit();
    }

    @PostUpdate
    public void onPostUpdate() {
        System.out.println("==================== order Update Hook is raised !!!");
    }

    // Order Aggregate에 Istio Lab를 위한 코드
    @PrePersist
    public void onPrePersist() {
        try {
            Thread.currentThread().sleep((long)(800 + Math.random() * 200));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @PreRemove
    public void onPreRemove(){
        OrderCanceled orderCanceled = new OrderCanceled();
        BeanUtils.copyProperties(this, orderCanceled);
        orderCanceled.publishAfterCommit();

        //Following code causes dependency to external APIs
        // it is NOT A GOOD PRACTICE. instead, Event-Policy mapping is recommended.

        mall.external.Cancellation cancellation = new mall.external.Cancellation();

        cancellation.setId(this.getId());
        cancellation.setOrderId(this.getId());
        cancellation.setStatus("DeliveryCancelled");

        // mappings goes here
        OrderApplication.applicationContext.getBean(mall.external.CancellationService.class)
            .cancel(cancellation);

        System.out.println("##### listener Ship : " + orderCanceled.toJson());
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }
    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
