package ir.jin724.videochat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.security.Provider;
import java.security.Security;
import java.util.Set;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;


@RunWith(MockitoJUnitRunner.class)
public class ProviderUtilTest {


    @Test
    public void allAlgorithm() {
        Provider[] providers = Security.getProviders();
        for (Provider provider : providers) {
            //Log.e("CRYPTO", "provider: " + provider.getName());
            Set<Provider.Service> services = provider.getServices();
            for (Provider.Service service : services) {
                // Log.e("CRYPTO", "  algorithm: " + service.getAlgorithm());
                assertNotNull(service);
                assertNotEquals(service.getAlgorithm(), "");
            }
        }

    }
}