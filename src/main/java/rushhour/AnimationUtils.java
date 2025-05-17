package rushhour;

import javafx.animation.*;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.effect.Glow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import rushhour.lib.*;

public class AnimationUtils {
    public static ParallelTransition createMoveAnimation(Move move, Rectangle carRect, double duration) {
        ParallelTransition animations = new ParallelTransition();
        
        // Store original color for restoration later
        Color originalColor = (Color) carRect.getFill();
        
        // 1. Initial scale effect to draw attention
        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(duration * 0.2), carRect);
        scaleUp.setToX(1.15);
        scaleUp.setToY(1.15);
        
        // 2. Create color cycling effect (Minecraft-style)
        Timeline colorCycle = new Timeline();
        colorCycle.setCycleCount(Timeline.INDEFINITE);
        
        // Create property to animate the color cycling
        DoubleProperty colorShift = new SimpleDoubleProperty(0);
        
        // Listen to property changes and update the car color
        colorShift.addListener((obs, oldVal, newVal) -> {
            // Get current progress (0.0 to 1.0)
            double progress = newVal.doubleValue();
            
            // Create cycling rainbow effect by shifting hue
            double baseHue = originalColor.getHue();
            double shiftedHue = (baseHue + progress * 360) % 360;
            
            // Create new color with increased saturation and brightness for more vibrant effect
            Color cycledColor = Color.hsb(
                shiftedHue,
                Math.min(1.0, originalColor.getSaturation() * 1.5), 
                Math.min(1.0, originalColor.getBrightness() * 1.2),
                originalColor.getOpacity()
            );
            
            // Apply new color to car rectangle
            carRect.setFill(cycledColor);
        });
        
        // Create keyframes to cycle through colors smoothly
        colorCycle.getKeyFrames().addAll(
            new KeyFrame(Duration.ZERO, new KeyValue(colorShift, 0.0)),
            new KeyFrame(Duration.millis(500), new KeyValue(colorShift, 1.0))
        );
        
        // 3. Add glow effect that intensifies during movement
        Bloom bloom = new Bloom(0.4);
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setBrightness(0.2);
        colorAdjust.setContrast(0.2);
        colorAdjust.setSaturation(0.5);
        bloom.setInput(colorAdjust);
        
        carRect.setEffect(bloom);
        
        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(duration * 0.2), carRect);
        scaleDown.setDelay(Duration.millis(duration * 0.8));
        scaleDown.setToX(1.0);
        scaleDown.setToY(1.0);
        
        colorCycle.play();
        
        Timeline resetAppearance = new Timeline(
            new KeyFrame(Duration.millis(duration), e -> {
                carRect.setFill(originalColor);
                carRect.setEffect(new DropShadow(4, 1, 1, Color.rgb(0, 0, 0, 0.3)));
                colorCycle.stop();
            })
        );
        
        // Combine all animations
        animations.getChildren().addAll(
            scaleUp,
            scaleDown,
            resetAppearance
        );
        
        return animations;
    }
    
    public static Timeline addHighlightEffect(Rectangle rect) {
        Glow glow = new Glow(0.8);
        DropShadow highlight = new DropShadow(12, Color.WHITE);
        highlight.setInput(glow);
        rect.setEffect(highlight);
        
        Timeline pulse = new Timeline(
            new KeyFrame(Duration.ZERO, 
                new KeyValue(rect.scaleXProperty(), 1.0),
                new KeyValue(rect.scaleYProperty(), 1.0)
            ),
            new KeyFrame(Duration.millis(500), 
                new KeyValue(rect.scaleXProperty(), 1.10),
                new KeyValue(rect.scaleYProperty(), 1.10)
            ),
            new KeyFrame(Duration.millis(1000), 
                new KeyValue(rect.scaleXProperty(), 1.0),
                new KeyValue(rect.scaleYProperty(), 1.0)
            )
        );
        pulse.setCycleCount(Animation.INDEFINITE);
        pulse.play();
        
        return pulse;
    }
    
    public static ParallelTransition createCelebrationEffect(Rectangle mainCar) {
        if (mainCar == null) return null;
        
        Color originalColor = (Color) mainCar.getFill();
        Effect originalEffect = mainCar.getEffect();
        
        ParallelTransition celebration = new ParallelTransition();
        
        Timeline rainbow = new Timeline();
        rainbow.setCycleCount(6);
        
        DoubleProperty hueShift = new SimpleDoubleProperty(0);
        
        hueShift.addListener((obs, oldVal, newVal) -> {
            double hue = (newVal.doubleValue() * 360) % 360;
            Color newColor = Color.hsb(hue, 0.9, 0.9);
            mainCar.setFill(newColor);
        });
        
        rainbow.getKeyFrames().addAll(
            new KeyFrame(Duration.ZERO, new KeyValue(hueShift, 0.0)),
            new KeyFrame(Duration.millis(500), new KeyValue(hueShift, 1.0))
        );
        
        ScaleTransition pulse = new ScaleTransition(Duration.millis(250), mainCar);
        pulse.setFromX(1.0);
        pulse.setFromY(1.0);
        pulse.setToX(1.2);
        pulse.setToY(1.2);
        pulse.setCycleCount(12);
        pulse.setAutoReverse(true);
        
        Bloom bloom = new Bloom(0.7);
        DropShadow gold = new DropShadow(20, Color.GOLD);
        gold.setInput(bloom);
        mainCar.setEffect(gold);
        
        celebration.getChildren().addAll(rainbow, pulse);
        
        celebration.setOnFinished(e -> {
            mainCar.setFill(originalColor);
            mainCar.setEffect(originalEffect != null ? originalEffect : 
                new DropShadow(4, 1, 1, Color.rgb(0, 0, 0, 0.3)));
            mainCar.setScaleX(1.0);
            mainCar.setScaleY(1.0);
        });
        
        return celebration;
    }
    
    public static void resetRectangleAppearance(Rectangle rect, Color originalColor) {
        if (rect == null) return;
        
        if (originalColor != null) {
            rect.setFill(originalColor);
        }
        
        rect.setEffect(new DropShadow(4, 1, 1, Color.rgb(0, 0, 0, 0.3)));
        rect.setScaleX(1.0);
        rect.setScaleY(1.0);
    }
}