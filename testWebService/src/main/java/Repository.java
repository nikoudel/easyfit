import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicLong;

public class Repository
{
	private final static List<Car> cars = Collections.synchronizedList(new ArrayList<Car>());
	private final static AtomicLong counter = new AtomicLong();

	public static Car addCar(Car car)
	{
		car.id = counter.incrementAndGet();

		cars.add(car);

		return car;
	}

	public static List<Car> getCars()
	{
		return cars;
	}
}