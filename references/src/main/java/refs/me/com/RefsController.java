package refs.me.com;

import android.support.annotation.Nullable;

import java.util.Timer;
import java.util.TimerTask;

import refs.me.com.keep.Reference;
import timber.log.Timber;

class RefsController {

    private static final int SHOW_DELAY_SECONDS = 20;
    private static final int SHOW_BACKOFF_COUNT = 16;

    public interface IReferenceOpener {
        void showReference(boolean marketLink, Reference reference);
    }

    private final IReferenceOpener referenceOpener;
    @Nullable
    private Reference activeReference = null;
    @Nullable
    private Timer refOpenTimer = null;
    private int currentShowCount = SHOW_BACKOFF_COUNT;

    RefsController(IReferenceOpener referenceOpener) {
        this.referenceOpener = referenceOpener;
    }

    void removeActiveReference() {
        this.activeReference = null;
        if (refOpenTimer != null) {
            refOpenTimer.cancel();
            refOpenTimer = null;
        }
    }

    boolean isReferencedApp(String applicationId) {
        return activeReference != null && activeReference.getApplicationId().contains(applicationId);
    }

    String getActiveRefId() {
        return activeReference != null ? activeReference.getId() : null;
    }

    void showReference(Reference reference) {
        this.activeReference = reference;
        if (refOpenTimer != null) {
            refOpenTimer.cancel();
        }
        refOpenTimer = new Timer();
        currentShowCount = SHOW_BACKOFF_COUNT;
        TimerTask task = createReferenceTimerTask();
        refOpenTimer.scheduleAtFixedRate(task, 0, SHOW_DELAY_SECONDS * 1000L);
    }

    private TimerTask createReferenceTimerTask() {
        return new TimerTask() {
            @Override
            public void run() {
                boolean showRef = activeReference != null && --currentShowCount >= 0;
                Timber.i("run -> showRef[%s]", showRef);
                if (showRef) {
                    referenceOpener.showReference(isMarketLink(activeReference.getLink()), activeReference);
                } else {
                    removeActiveReference();
                }
            }
        };
    }

    private boolean isMarketLink(String url) {
        return url.contains("play.google.com/store/apps/details");
    }
}