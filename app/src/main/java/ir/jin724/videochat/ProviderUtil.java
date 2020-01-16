package ir.jin724.videochat;

import android.util.Log;

import java.security.Provider;
import java.security.Security;
import java.util.Set;

class ProviderUtil {
    static void allAlgorithm() {
        Provider[] providers = Security.getProviders();
        for (Provider provider : providers) {
            Log.e("CRYPTO", "provider: " + provider.getName());
            Set<Provider.Service> services = provider.getServices();
            for (Provider.Service service : services) {
                Log.e("CRYPTO", "  algorithm: " + service.getAlgorithm());
            }
        }
    }
}