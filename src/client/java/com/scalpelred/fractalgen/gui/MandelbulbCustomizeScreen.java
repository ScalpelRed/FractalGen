package com.scalpelred.fractalgen.gui;

import com.scalpelred.fractalgen.ComplexNumber3;
import com.scalpelred.fractalgen.mandelbulb.MandelbulbChunkGenerator;
import com.scalpelred.fractalgen.mandelbulb.MandelbulbSettings;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.CustomizeBuffetLevelScreen;
import net.minecraft.client.gui.screen.world.CustomizeFlatLevelScreen;
import net.minecraft.client.gui.screen.world.PresetsScreen;
import net.minecraft.client.gui.tab.GridScreenTab;
import net.minecraft.client.gui.tab.Tab;
import net.minecraft.client.gui.tab.TabManager;
import net.minecraft.client.gui.widget.*;
import net.minecraft.client.world.GeneratorOptionsHolder;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import net.minecraft.world.biome.source.MultiNoiseBiomeSourceParameterList;
import net.minecraft.world.biome.source.MultiNoiseBiomeSourceParameterLists;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionOptionsRegistryHolder;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Function;

public class MandelbulbCustomizeScreen extends Screen {

    private static final Text TEXT_TITLE = Text.translatable("gui.customize.mandelbulb.title");
    private static final Text TEXT_TITLE_GEOMETRY_OVERWORLD = Text.translatable("gui.customize.mandelbulb.geometry_params.overworld");
    private static final Text TEXT_TITLE_GEOMETRY_NETHER = Text.translatable("gui.customize.mandelbulb.geometry_params.nether");
    private static final Text TEXT_TITLE_GEOMETRY_THEEND = Text.translatable("gui.customize.mandelbulb.geometry_params.the_end");
    private static final Text TEXT_TITLE_BIOME_OVERWORLD = Text.translatable("gui.customize.mandelbulb.biome_params.overworld");
    private static final Text TEXT_TITLE_BIOME_NETHER = Text.translatable("gui.customize.mandelbulb.biome_params.nether");
    private static final Text TEXT_TITLE_BIOME_THEEND = Text.translatable("gui.customize.mandelbulb.biome_params.the_end");

    private final CreateWorldScreen parent;

    MandelbulbSettings overworldGeometrySettings;
    MandelbulbSettings netherGeometrySettings;
    MandelbulbSettings theEndGeometrySettings;

    private final TabManager tabManager = new TabManager(this::addDrawableChild, this::remove);
    private MandelbulbTab overworldGeometryTab;
    private MandelbulbTab netherGeometryTab;
    private MandelbulbTab theEndGeometryTab;

    public MandelbulbCustomizeScreen(CreateWorldScreen parent,
                                     GeneratorOptionsHolder generatorOptionsHolder) {
        super(TEXT_TITLE);
        this.parent = parent;

        DimensionOptionsRegistryHolder dimensionOptionsRegistry = generatorOptionsHolder.selectedDimensions();

        Optional<DimensionOptions> overworld = dimensionOptionsRegistry.getOrEmpty(DimensionOptions.OVERWORLD);
        if (overworld.isPresent()) {
            MandelbulbChunkGenerator chunkGenerator = (MandelbulbChunkGenerator)overworld.get().chunkGenerator();
            overworldGeometrySettings = chunkGenerator.getSettings();
        }

        Optional<DimensionOptions> nether = dimensionOptionsRegistry.getOrEmpty(DimensionOptions.NETHER);
        if (nether.isPresent()) {
            MandelbulbChunkGenerator chunkGenerator = (MandelbulbChunkGenerator)nether.get().chunkGenerator();
            netherGeometrySettings = chunkGenerator.getSettings();
        }

        Optional<DimensionOptions> theEnd = dimensionOptionsRegistry.getOrEmpty(DimensionOptions.END);
        if (theEnd.isPresent()) {
            MandelbulbChunkGenerator chunkGenerator = (MandelbulbChunkGenerator)theEnd.get().chunkGenerator();
            theEndGeometrySettings = chunkGenerator.getSettings();
        }
    }

    @Override
    protected void init() {
        super.init();
        assert client != null;

        ArrayList<Tab> tabList = new ArrayList<>();
        tabList.add(new HelpTab());
        // in case that... um... any mod removes default dimensions... it's unlikely, but it may happen.
        if (overworldGeometrySettings != null) tabList.add(overworldGeometryTab = new MandelbulbTab(
                TEXT_TITLE_GEOMETRY_OVERWORLD, overworldGeometrySettings));
        if (netherGeometrySettings != null) tabList.add(netherGeometryTab = new MandelbulbTab(
                TEXT_TITLE_GEOMETRY_NETHER, netherGeometrySettings));
        if (theEndGeometrySettings != null) tabList.add(theEndGeometryTab = new MandelbulbTab(
                TEXT_TITLE_GEOMETRY_THEEND, theEndGeometrySettings));
        // actually, I don't know if they can be removed

        TabNavigationWidget tabs = TabNavigationWidget.builder(this.tabManager, this.width)
                .tabs(tabList.toArray(new Tab[0])).build();
        this.addDrawableChild(tabs);

        GridWidget grid = new GridWidget().setColumnSpacing(10);
        GridWidget.Adder adder = grid.createAdder(2);

        ButtonWidget buttonWidget = ButtonWidget.builder(ScreenTexts.DONE, button -> {
            if (overworldGeometrySettings != null) overworldGeometryTab.saveValues();
            if (netherGeometryTab != null) netherGeometryTab.saveValues();
            if (theEndGeometryTab != null) theEndGeometryTab.saveValues();
            client.setScreen(parent);
        }).build();
        adder.add(buttonWidget, 1);
        addDrawableChild(buttonWidget);

        buttonWidget = ButtonWidget.builder(ScreenTexts.CANCEL, button -> {
            client.setScreen(parent);
        }).build();
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
        parent.getWorldCreator().applyModifier((dynamicRegistryManager, dimensionRegistryHolder) -> {
            RegistryEntryLookup<MultiNoiseBiomeSourceParameterList> paramRegistry
                    = dynamicRegistryManager.getWrapperOrThrow(RegistryKeys.MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST);
            RegistryEntry.Reference<MultiNoiseBiomeSourceParameterList> params
                    = paramRegistry.getOrThrow(MultiNoiseBiomeSourceParameterLists.OVERWORLD);

            MandelbulbChunkGenerator chunkGenerator = new MandelbulbChunkGenerator(
                    MultiNoiseBiomeSource.create(params),
                    overworldGeometrySettings
            );
            return dimensionRegistryHolder.with(dynamicRegistryManager, chunkGenerator);
        });
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
        private final MandelbulbSettings settings;

        MandelbulbTab(Text title, MandelbulbSettings settings) {
            super(title);
            this.settings = settings;
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

            powerField.setText(String.valueOf(settings.getPower()));
            iterationsField.setText(String.valueOf(settings.getIterations()));
            postIterationsField.setText(String.valueOf(settings.getPostIterations()));

            ComplexNumber3 vec = settings.getScale();
            scaleXField.setText(String.valueOf(vec.x));
            scaleYField.setText(String.valueOf(vec.y));
            scaleZField.setText(String.valueOf(vec.z));

            vec = settings.getRotation();
            rotationXField.setText(String.valueOf(vec.x));
            rotationYField.setText(String.valueOf(vec.y));
            rotationZField.setText(String.valueOf(vec.z));

            vec = settings.getTranslation();
            translationXField.setText(String.valueOf(vec.x));
            translationYField.setText(String.valueOf(vec.y));
            translationZField.setText(String.valueOf(vec.z));
        }

        public void saveValues() {

            settings.setPower(Double.parseDouble(powerField.getText()));
            settings.setIterations(Integer.parseInt(iterationsField.getText()));
            settings.setPostIterations(Integer.parseInt(postIterationsField.getText()));

            settings.setScale(ComplexNumber3.parse(
                    scaleXField.getText(),
                    scaleYField.getText(),
                    scaleZField.getText()
            ));

            settings.setRotation(ComplexNumber3.parse(
                    rotationXField.getText(),
                    rotationYField.getText(),
                    rotationZField.getText()
            ));

            settings.setTranslation(ComplexNumber3.parse(
                    translationXField.getText(),
                    translationYField.getText(),
                    translationZField.getText()
            ));
        }
    }

    private class HelpTab extends GridScreenTab {

        private final MultilineTextWidget text;

        public HelpTab() {
            super(Text.translatable("gui.customize.mandelbulb.help_title"));
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