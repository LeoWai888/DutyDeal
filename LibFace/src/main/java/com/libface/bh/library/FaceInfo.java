//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.libface.bh.library;

import android.graphics.Rect;
import com.sensetime.library.finance.liveness.DetectInfo;

public final class FaceInfo {
    private DetectInfo detectInfo = null;

    FaceInfo(DetectInfo detectInfo) {
        this.detectInfo = detectInfo;
    }

    public boolean isPass() {
        return this.detectInfo.isPass();
    }

    public Rect getFaceRect() {
        return this.detectInfo.getFaceRect();
    }

    public FaceInfo.FaceDistance getFaceDistance() {
        return this.detectInfo != null && this.detectInfo.getFaceDistance() != null ? FaceInfo.FaceDistance.valueOf(this.detectInfo.getFaceDistance().name()) : FaceInfo.FaceDistance.UNKNOWN;
    }

    public FaceInfo.FaceState getFaceState() {
        return this.detectInfo != null && this.detectInfo.getFaceState() != null ? FaceInfo.FaceState.valueOf(this.detectInfo.getFaceState().name()) : FaceInfo.FaceState.UNKNOWN;
    }

    public static enum FaceDistance {
        NORMAL,
        CLOSE,
        FAR,
        UNKNOWN;

        private FaceDistance() {
        }
    }

    public static enum FaceState {
        NORMAL,
        MISSED,
        CHANGED,
        OUT_OF_BOUND,
        UNKNOWN;

        private FaceState() {
        }
    }
}
