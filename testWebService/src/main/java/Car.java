import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Length;
import java.util.Date;

public class Car
{
    public Car()
    {
        // Jackson deserialization
    }

    @JsonProperty
    public long id;

    @JsonProperty
    public String make;

    @JsonProperty
    public String model;

    @JsonProperty
    public String color;

    @JsonProperty
    public Date registrationDate;

    @JsonProperty
    public int mileage;

    @JsonProperty
    public Fuel fuel;
}