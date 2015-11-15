import com.codahale.metrics.health.HealthCheck;

// This class is only for hiding the nasty warning
// appearing during application startup.

public class EmptyHealthCheck extends HealthCheck
{
    @Override
    protected Result check() throws Exception 
    {
        return Result.healthy();
    }
}