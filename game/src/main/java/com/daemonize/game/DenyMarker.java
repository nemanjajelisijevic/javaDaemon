package com.daemonize.game;

import com.daemonize.daemonengine.consumer.Consumer;
import com.daemonize.daemonengine.implementations.SideQuestDaemonEngine;
import com.daemonize.daemonengine.quests.InterruptibleSleepSideQuest;
import com.daemonize.graphics2d.images.Image;
import com.daemonize.graphics2d.scene.views.ImageView;

public class DenyMarker extends SideQuestDaemonEngine {

    private boolean deny;
    private Image deniedImage;
    private Image endImage;
    private ImageView view;
    private boolean showEndImage;
    private Consumer interruptConsumer;

    public DenyMarker setView(ImageView view, boolean showEndImage) {
        this.view = view;
        this.showEndImage = showEndImage;
        return this;
    }

    public boolean denied() {
        return deny;
    }

    public DenyMarker(Consumer drawConsumer, Consumer interruptConsumer, Image deniedImage, Image endImage) {
        this.consumer = drawConsumer;
        this.interruptConsumer = interruptConsumer;
        this.deniedImage = deniedImage;
        this.endImage = endImage;
        setName("Denied Marker");
    }

    @Override
    public DenyMarker start() {
        deny = true;
        consumer.consume(() -> view.setImage(deniedImage).show());
        setSideQuest(consumer, new InterruptibleSleepSideQuest<Boolean>() {

            private int cnt = 0;

            @Override
            public Boolean pursue() throws Exception {
                if (cnt++ <= 6) {
                    if (cnt % 2 == 0) return false;
                    else return true;
                } else
                    throw new InterruptedException();
            }

        }).setSleepInterval(300).setClosure(showDeniedMarker -> {
            if (showDeniedMarker.runtimeCheckAndGet())
                view.show();
            else
                view.hide();
        }).onInterrupt(interruptConsumer, () -> {
            this.stop();
            deny = false;
            consumer.consume(() -> {
                view.setImage(endImage);
                if (showEndImage)
                    view.show();
                else
                    view.hide();
            });
        });
        super.start();
        return this;
    }
}
