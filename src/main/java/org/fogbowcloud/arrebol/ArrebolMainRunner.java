package org.fogbowcloud.arrebol;

import org.fogbowcloud.arrebol.datastore.managers.QueueDBManager;
import org.fogbowcloud.arrebol.datastore.repositories.DefaultQueueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;


public class ArrebolMainRunner implements CommandLineRunner {

    @Autowired
    private DefaultQueueRepository defaultQueueRepository;

    @Override
    public void run(String... strings) throws Exception {
        QueueDBManager.getInstance().setDefaultQueueRepository(defaultQueueRepository);
    }
}
