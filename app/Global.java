import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import actor.BbtshopsStat;
import actor.PushPostOrderActor;
import actor.StatActor;
import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.Play;
import play.libs.Akka;
import play.libs.F.Promise;
import play.libs.Json;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Http.RequestHeader;
import play.mvc.Result;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import form.DateBetween;
import scala.concurrent.duration.Duration;

/**
 * Application wide behaviour. We establish a Spring application context for the dependency injection system and
 * configure Spring Data.
 */
public class Global extends GlobalSettings {

    private static Logger.ALogger LOGGER = Logger.of(Global.class);

    /**
     * Declare the application context to be used - a Java annotation based application context requiring no XML.
     */

    /**
     * Sync the context lifecycle with Play's.
     */
    @Override
    public void onStart(final Application app) {
        super.onStart(app);

        configJson();

        //DateBetween 注册
        LOGGER.info("register DateBetween");
        DateBetween.register();
        boolean doactor = Play.application().configuration().getBoolean("doactor", false);
		if(doactor){
            Akka.system().scheduler().schedule(
                    Duration.create(StatActor.nextExecutionInSeconds(4, 0), TimeUnit.SECONDS),
                    Duration.create(24, TimeUnit.HOURS), StatActor.myActor, "ACT", Akka.system().dispatcher(), null);//每天
            Akka.system().scheduler().schedule(
                    Duration.create(StatActor.nextExecutionInSeconds(2, 0), TimeUnit.SECONDS),     //每天2点
                    Duration.create(24, TimeUnit.HOURS),
                    BbtshopsStat.myActor,
                    "ACT",
                    Akka.system().dispatcher(),
                    null
            );
            Akka.system().scheduler().schedule(Duration.create(0, TimeUnit.MILLISECONDS),
                    Duration.create(1, TimeUnit.MINUTES),
                    PushPostOrderActor.myActor, "ACT", Akka.system().dispatcher(), null);//5每分钟
		}


//        (new Thread(new FreshTaskTrigger())).start();
    }

    @Override
    public Action<?> onRequest(final Http.Request request, Method actionMethod) {
        LOGGER.info("request {} and request method is {}", request.uri(),request);
        return super.onRequest(request, actionMethod);
    }

    /**
     * Sync the context lifecycle with Play's.
     */
    @Override
    public void onStop(final Application app) {
        // This will call any destruction lifecycle methods and then release the beans e.g. @PreDestroy
        super.onStop(app);
    }

    
    
    
    @Override
	public Promise<Result> onBadRequest(RequestHeader paramRequestHeader,
			String paramString) {
		// TODO Auto-generated method stub
		return super.onBadRequest(paramRequestHeader, paramString);
	}

	/*@Override
	public Promise<Result> onError(Http.RequestHeader request, Throwable t) {
		LOGGER.error("error request " + request.uri(), t);
        if (request.uri().startsWith("/api")) {
            return F.Promise.promise(new F.Function0<Result>() {
                @Override
                public Result apply() throws Throwable {
                    DefaultServiceVO vo = new DefaultServiceVO();
                    vo.error("global.systemError");
                    return Results.ok(vo.toJson());
                }
            });
        } else {
            return F.Promise.pure(Action.redirect("/500"));
        }
	}*/

	@Override
	public Promise<Result> onHandlerNotFound(RequestHeader paramRequestHeader) {
		// TODO Auto-generated method stub
		return super.onHandlerNotFound(paramRequestHeader);
	}

	private void configJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.writerWithDefaultPrettyPrinter();
        Json.setObjectMapper(objectMapper);
    }

}
