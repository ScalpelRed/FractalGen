package com.scalpelred.fractalgen.gui;

import com.google.common.collect.ImmutableMap;
import com.scalpelred.fractalgen.ComplexNumber3;
import com.scalpelred.fractalgen.mandelbulb.MandelbulbChunkGenerator;
import com.scalpelred.fractalgen.mandelbulb.MandelbulbSettings;
import com.scalpelred.fractalgen.mandelbulb.MandelbulbSettingsMutable;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.tab.GridScreenTab;
import net.minecraft.client.gui.tab.Tab;
import net.minecraft.client.gui.tab.TabManager;
import net.minecraft.client.gui.widget.*;
import net.minecraft.client.world.GeneratorOptionsHolder;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.dimension.*;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Function;

public class MandelbulbCustomizeScreen extends Screen {

    private static final Text TEXT_TITLE = Text.translatable("gui.customize.mandelbulb.title");
    private static final Text TEXT_TITLE_OVERWORLD = Text.translatable("gui.customize.mandelbulb.title.overworld");
    private static final Text TEXT_TITLE_NETHER = Text.translatable("gui.customize.mandelbulb.title.nether");
    private static final Text TEXT_TITLE_THEEND = Text.translatable("gui.customize.mandelbulb.title.the_end");

    private final CreateWorldScreen parent;

    MandelbulbSettings overworldSettings;
    MandelbulbSettings netherSettings;
    MandelbulbSettings theEndSettings;

    private final TabManager tabManager = new TabManager(this::addDrawableChild, this::remove);
    private MandelbulbTab overworldTab;
    private MandelbulbTab netherTab;
    private MandelbulbTab theEndTab;

    public MandelbulbCustomizeScreen(CreateWorldScreen parent,
                                     GeneratorOptionsHolder generatorOptionsHolder) {
        super(TEXT_TITLE);
        this.parent = parent;

        DimensionOptionsRegistryHolder dimensionOptionsRegistry = generatorOptionsHolder.selectedDimensions();

        Optional<DimensionOptions> overworld = dimensionOptionsRegistry.getOrEmpty(DimensionOptions.OVERWORLD);
        if (overworld.isPresent()) {
            MandelbulbChunkGenerator chunkGenerator = (MandelbulbChunkGenerator)overworld.get().chunkGenerator();
            overworldSettings = chunkGenerator.getSettings();
        }

        Optional<DimensionOptions> nether = dimensionOptionsRegistry.getOrEmpty(DimensionOptions.NETHER);
        if (nether.isPresent()) {
            MandelbulbChunkGenerator chunkGenerator = (MandelbulbChunkGenerator)nether.get().chunkGenerator();
            netherSettings = chunkGenerator.getSettings();
        }

        Optional<DimensionOptions> theEnd = dimensionOptionsRegistry.getOrEmpty(DimensionOptions.END);
        if (theEnd.isPresent()) {
            MandelbulbChunkGenerator chunkGenerator = (MandelbulbChunkGenerator)theEnd.get().chunkGenerator();
            theEndSettings = chunkGenerator.getSettings();
        }
    }

    @Override
    protected void init() {
        super.init();
        assert client != null;

        ArrayList<Tab> tabList = new ArrayList<>();
        tabList.add(new HelpTab());
        // in case that... um... any mod removes default dimensions... it's unlikely, but it may happen.
        if (overworldSettings != null) tabList.add(overworldTab = new MandelbulbTab(
                TEXT_TITLE_OVERWORLD, overworldSettings));
        if (netherSettings != null) tabList.add(netherTab = new MandelbulbTab(
                TEXT_TITLE_NETHER, netherSettings));
        if (theEndSettings != null) tabList.add(theEndTab = new MandelbulbTab(
                TEXT_TITLE_THEEND, theEndSettings));
        // actually, I don't know if they can be removed

        TabNavigationWidget tabs = TabNavigationWidget.builder(this.tabManager, this.width)
                .tabs(tabList.toArray(new Tab[0])).build();
        this.addDrawableChild(tabs);

        GridWidget grid = new GridWidget().setColumnSpacing(10);
        GridWidget.Adder adder = grid.createAdder(2);

        ButtonWidget buttonWidget = ButtonWidget.builder(ScreenTexts.DONE, button -> {
            if (overworldSettings != null) overworldSettings = overworldTab.saveValues();
            if (netherSettings != null) netherSettings = netherTab.saveValues();
            if (theEndSettings != null) theEndSettings = theEndTab.saveValues();
            applyModifier();
            client.setScreen(parent);
        }).build();
        adder.add(buttonWidget, 1);
        addDrawableChild(buttonWidget);

        buttonWidget = ButtonWidget.builder(ScreenTexts.CANCEL, button -> client.setScreen(parent)).build();
        adder.add(buttonWidget, 1);
        addDrawableChild(buttonWidget);

        tabs.selectTab(0, false);
        tabs.setWidth(this.width);
        tabs.init();
        grid.refreshPositions();
        SimplePositioningWidget.setPos(grid, 0, this.height - GuiUtil.BUTTON_HEIGHT * 2, this.width,
                GuiUtil.BUTTON_HEIGHT * 2);
        int offset = tabs.getNavigationFocus().getBottom();
        ScreenRect screenRect = new ScreenRect(0, offset, this.width, grid.getY() - offset);
        this.tabManager.setTabArea(screenRect);
    }

    private void applyModifier() {
        GeneratorOptionsHolder.RegistryAwareModifier modifier
                = (dynamicRegistryManager, dimensionsRegistryHolder) -> {

            Registry<DimensionType> dimTypeRegistry = dynamicRegistryManager.get(RegistryKeys.DIMENSION_TYPE);
            Registry<Biome> biomeRegistry = dynamicRegistryManager.get(RegistryKeys.BIOME);

            ImmutableMap.Builder<RegistryKey<DimensionOptions>, DimensionOptions> builder = ImmutableMap.builder();

            builder.put(DimensionOptions.OVERWORLD, new DimensionOptions(
                    dimTypeRegistry.getEntry(dimTypeRegistry.get(DimensionTypes.OVERWORLD.getValue())),
                    new MandelbulbChunkGenerator(
                            new FixedBiomeSource(biomeRegistry.getEntry(biomeRegistry.get(
                                    overworldSettings.getBiome()))), overworldSettings)
            ));

            builder.put(DimensionOptions.NETHER, new DimensionOptions(
                    dimTypeRegistry.getEntry(dimTypeRegistry.get(DimensionTypes.THE_NETHER.getValue())),
                    new MandelbulbChunkGenerator(
                            new FixedBiomeSource(biomeRegistry.getEntry(biomeRegistry.get(
                                    netherSettings.getBiome()))), netherSettings)
            ));

            builder.put(DimensionOptions.END, new DimensionOptions(
                    dimTypeRegistry.getEntry(dimTypeRegistry.get(DimensionTypes.THE_END.getValue())),
                    new MandelbulbChunkGenerator(
                            new FixedBiomeSource(biomeRegistry.getEntry(biomeRegistry.get(
                                    theEndSettings.getBiome()))), theEndSettings)
            ));

            return new DimensionOptionsRegistryHolder(builder.buildKeepingLast());
        };

        parent.getWorldCreator().applyModifier(modifier);
    }


    private class MandelbulbTab extends GridScreenTab {
        private static final Text TEXT_POWER = Text.translatable("gui.customize.mandelbulb.power");
        private static final Text TEXT_SCALE = Text.translatable("gui.customize.mandelbulb.scale");
        private static final Text TEXT_ROTATION = Text.translatable("gui.customize.mandelbulb.rotation");
        private static final Text TEXT_TRANSLATION = Text.translatable("gui.customize.mandelbulb.translation");
        private static final Text TEXT_ITERATIONS = Text.translatable("gui.customize.mandelbulb.iterations");
        private static final Text TEXT_POST_ITERATIONS = Text.translatable("gui.customize.mandelbulb.post_iterations");

        private final TextFieldWidget powerField;
        private final TextFieldWidget scaleXField;
        private final TextFieldWidget scaleYField;
        private final TextFieldWidget scaleZField;
        private final TextFieldWidget rotationXField;
        private final TextFieldWidget rotationYField;
        private final TextFieldWidget rotationZField;
        private final TextFieldWidget translationXField;
        private final TextFieldWidget translationYField;
        private final TextFieldWidget translationZField;
        private final TextFieldWidget iterationsField;
        private final TextFieldWidget postIterationsField;

        private final GridWidget.Adder adder;
        private final MandelbulbSettingsMutable settings;

        MandelbulbTab(Text title, MandelbulbSettings settings) {
            super(title);
            this.settings = new MandelbulbSettingsMutable(settings);
            this.grid.setSpacing(5);
            this.grid.setColumnSpacing(10);
            adder = this.grid.createAdder(2);

            Function<TextFieldWidget, Boolean> doubleVerifier = c -> GuiUtil.canParseDouble(c.getText());

            powerField = addEntry(TEXT_POWER, doubleVerifier);
            iterationsField = addEntry(TEXT_ITERATIONS, doubleVerifier);
            postIterationsField = addEntry(TEXT_POST_ITERATIONS, doubleVerifier);
            addEmpty();
            scaleXField = addEntry(Text.empty(), doubleVerifier);
            scaleYField = addEntry(TEXT_SCALE, doubleVerifier);
            scaleZField = addEntry(Text.empty(), doubleVerifier);
            addEmpty();
            rotationXField = addEntry(Text.empty(), doubleVerifier);
            rotationYField = addEntry(TEXT_ROTATION, doubleVerifier);
            rotationZField = addEntry(Text.empty(), doubleVerifier);
            addEmpty();
            translationXField = addEntry(Text.empty(), doubleVerifier);
            translationYField = addEntry(TEXT_TRANSLATION, doubleVerifier);
            translationZField = addEntry(Text.empty(), doubleVerifier);

            grid.refreshPositions();
            SimplePositioningWidget.setPos(grid, 0, 0, parent.width, parent.height, 0.5f, 0.5f);

            resetValues();
        }

        private TextFieldWidget addEntry(Text nameText, Function<TextFieldWidget, Boolean> verifyFunc) {
            TextWidget text = new TextWidget(GuiUtil.BUTTON_WIDTH * 2, GuiUtil.BUTTON_HEIGHT, nameText,
                    MandelbulbCustomizeScreen.this.textRenderer);
            text.alignRight();
            adder.add(text, 1);

            TextFieldWidget res = new TextFieldWidget(MandelbulbCustomizeScreen.this.textRenderer,
                    GuiUtil.BUTTON_WIDTH, GuiUtil.BUTTON_HEIGHT, Text.of(""));
            adder.add(res, 1);
            return res;
        }

        private void addEmpty() {
            adder.add(new EmptyWidget(0, GuiUtil.BUTTON_HEIGHT / 4), 2);
        }

        private void resetValues() {

            powerField.setText(String.valueOf(settings.power));
            iterationsField.setText(String.valueOf(settings.iterations));
            postIterationsField.setText(String.valueOf(settings.postIterations));

            ComplexNumber3 vec = settings.scale;
            scaleXField.setText(String.valueOf(vec.x));
            scaleYField.setText(String.valueOf(vec.y));
            scaleZField.setText(String.valueOf(vec.z));

            vec = settings.rotation;
            rotationXField.setText(String.valueOf(vec.x));
            rotationYField.setText(String.valueOf(vec.y));
            rotationZField.setText(String.valueOf(vec.z));

            vec = settings.translation;
            translationXField.setText(String.valueOf(vec.x));
            translationYField.setText(String.valueOf(vec.y));
            translationZField.setText(String.valueOf(vec.z));
        }

        public MandelbulbSettings saveValues() {

            settings.power = Double.parseDouble(powerField.getText());
            settings.iterations = Integer.parseInt(iterationsField.getText());
            settings.postIterations = Integer.parseInt(postIterationsField.getText());

            settings.scale = ComplexNumber3.parse(
                    scaleXField.getText(),
                    scaleYField.getText(),
                    scaleZField.getText()
            );

            settings.rotation = ComplexNumber3.parse(
                    rotationXField.getText(),
                    rotationYField.getText(),
                    rotationZField.getText()
            );

            settings.translation = ComplexNumber3.parse(
                    translationXField.getText(),
                    translationYField.getText(),
                    translationZField.getText()
            );

            return settings.toImmutable();
        }
    }

    private class HelpTab extends GridScreenTab {

        private final MultilineTextWidget text;

        public HelpTab() {
            super(Text.translatable("gui.customize.mandelbulb.title.help"));
            MandelbulbCustomizeScreen parent = MandelbulbCustomizeScreen.this;
            GridWidget.Adder adder = grid.createAdder(1);

            text = new MultilineTextWidget(
                    Text.translatable("gui.customize.mandelbulb.help"),
                    parent.textRenderer);
            text.setMaxWidth(parent.width * 8 / 10);
            adder.add(text, 1);
        }
    }
}


/*
PARAMS:
- power (1)
- scale (3)
- rotation (3)
- translation (3)
- iterations (1)
- post-iterations (1)
- <buttons>
==========
20 * 17 = 340
5cm/4 * 17 = 21cm
 */