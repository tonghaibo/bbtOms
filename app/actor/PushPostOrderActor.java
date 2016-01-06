package actor;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import models.order.PostOrder;
import play.Logger;
import play.Logger.ALogger;
import play.libs.Akka;
import services.order.PostOrderService;

import java.util.List;

/**
 * 定时向同城配送员推送同城单
 * 
 * @author luobotao
 * @Date 2015年9月16日
 */
public class PushPostOrderActor extends UntypedActor {
	private final ALogger logger = Logger.of(PushPostOrderActor.class);

	public static ActorRef myActor = Akka.system().actorOf(Props.create(PushPostOrderActor.class));
	private PostOrderService postOrderService =  PostOrderService.instance;

	@Override
	public void onReceive(Object message) throws Exception {
		if ("ACT".equals(message)) {
			push();
		}
	}

	/**
	 * 查找同城订单表，并进行推送操作
	 */
	private void push() {
		//状态为1 （0未支付，1：待接单，2：待揽收，3：待配送，4：已完成（已送达），5：已完成（有问题））
		List<PostOrder> postOrderList =postOrderService.getlistBystatus(1);//待接单的订单
		for(PostOrder postOrder:postOrderList){
			postOrderService.sendpostpush(postOrder.getId());
		}

	}
}
