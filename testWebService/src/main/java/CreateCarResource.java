import com.codahale.metrics.annotation.Timed;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/CreateCar.json")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CreateCarResource
{
    @POST
    @Timed
    public Car post(Car car)
    {
        return Repository.addCar(car);
    }
}