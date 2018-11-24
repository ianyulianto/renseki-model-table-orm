import com.renseki.orm.model.ModelAggregation;
import com.renseki.orm.descriptor.DescriptorAggregation;
import org.testng.annotations.Test;


public class FooTest {

    @Test
    public void test() {
        final String packageName = "com.efitrac.module";

        ModelAggregation aggregation = new ModelAggregation.Builder(packageName)
            .build();

        DescriptorAggregation moduleAggregation = new DescriptorAggregation.Builder()
            .build();

        String asd = "";
        String qwe = "";
    }
}
