package hu.arezner;

import com.atlassian.bamboo.specs.api.builders.plan.Plan;
import com.atlassian.bamboo.specs.api.util.EntityPropertiesBuilders;
import java.util.logging.Logger;
import org.junit.Test;

public class ReziBambooPlanSpecTest {
    
    private static final Logger LOG = Logger.getLogger(ReziBambooPlanSpecTest.class.getName());
    
    @Test
    public void checkYourPlanOffline() {
        Plan plan = new ReziBambooPlanSpec().createPlan();

        EntityPropertiesBuilders.build(plan);
    }
    
}
