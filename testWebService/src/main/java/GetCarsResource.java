import com.google.common.base.Optional;
import com.codahale.metrics.annotation.Timed;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

@Path("/Cars.json")
@Produces(MediaType.APPLICATION_JSON)
public class GetCarsResource
{
    @GET
    @Timed
    public List<Car> get(
        @QueryParam("make") Optional<String> make,
        @QueryParam("color") Optional<String> color)
    {
        List<Car> cars = Repository.getCars();

        if(make.isPresent())
        {
            cars = filterByMake(cars, make.get());
        }

        if(color.isPresent())
        {
            cars = filterByColor(cars, color.get());
        }

        return cars;
    }

    private List<Car> filterByMake(List<Car> cars, String make)
    {
        List<Car> filtered = new ArrayList<Car>();

        for (Car car : cars)
        {
            if(make.equals(car.make))
            {
                filtered.add(car);
            }
        }

        return filtered;
    }

    private List<Car> filterByColor(List<Car> cars, String color)
    {
        List<Car> filtered = new ArrayList<Car>();

        for (Car car : cars)
        {
            if(color.equals(car.color))
            {
                filtered.add(car);
            }
        }

        return filtered;
    }
}