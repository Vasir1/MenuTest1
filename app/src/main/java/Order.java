import com.example.hennessee.menutest.MenuItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hennessee on 8/9/2014.
 */
public class Order {

    public List<MenuItem> OrderItems = new ArrayList<MenuItem>();

    public void Order()
    {
    }

    public void Order(List<MenuItem> orderItems)
    {
        OrderItems = orderItems;
    }
}
