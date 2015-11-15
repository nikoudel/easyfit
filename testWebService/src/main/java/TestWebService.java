import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.Configuration;

public class TestWebService extends Application<Configuration>
{
    public static void main(String[] args) throws Exception
    {
        new TestWebService().run(args);
    }

    @Override
    public String getName()
    {
        return "easyFit-system-under-test";
    }

    @Override
    public void initialize(Bootstrap<Configuration> bootstrap)
    {
        // nothing to do yet
    }

    @Override
    public void run(Configuration configuration, Environment environment)
    {
        environment.healthChecks().register("emptyCheckToHideWarning", new EmptyHealthCheck());

        environment.jersey().register(new GetCarsResource());
        environment.jersey().register(new CreateCarResource());
    }
}