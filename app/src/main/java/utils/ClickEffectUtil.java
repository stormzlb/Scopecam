package utils;

import android.graphics.Color;
import android.view.View;

import com.balysv.materialripple.MaterialRippleLayout;

public class ClickEffectUtil {
	public static void set(View v) {
		MaterialRippleLayout.on(v).rippleColor(Color.parseColor("#3994E4"))
				.rippleAlpha(0.2f).rippleHover(true).rippleOverlay(true)
				.create();
	}
}
