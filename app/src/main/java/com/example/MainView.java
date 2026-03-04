package com.example;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.List;

@Route("")
@PageTitle("Trie Word Predictor")
public class MainView extends VerticalLayout {

    private final TrieController trieController;

    private TextArea inputField;
    private Div nextWordResults;
    private Div nextCharResults;
    private Div wordAnalysis;

    public MainView(TrieController trieController) {
        this.trieController = trieController;

        setSizeFull();
        setPadding(false);
        setSpacing(false);
        addClassName("main-container");

        createBackground();
        createHeader();

        HorizontalLayout mainContent = new HorizontalLayout();
        mainContent.setWidthFull();
        mainContent.setMaxWidth("1400px");
        mainContent.getStyle()
            .set("margin", "0 auto")
            .set("display", "flex")
            .set("flex-direction", "row")
            .set("position", "relative")
            .set("z-index", "2");
        mainContent.setPadding(true);
        mainContent.setSpacing(true);
        mainContent.setAlignItems(com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment.START);

        VerticalLayout leftSection = new VerticalLayout();
        leftSection.addClassName("left-section");
        leftSection.getStyle()
            .set("flex", "1 1 auto")
            .set("min-width", "0");
        leftSection.setPadding(false);
        leftSection.setSpacing(true);

        createInputSection(leftSection);
        createPredictionsSection(leftSection);

        VerticalLayout rightSection = new VerticalLayout();
        rightSection.addClassName("right-section");
        rightSection.getStyle()
            .set("flex", "0 0 450px")
            .set("max-width", "450px");
        rightSection.setPadding(false);

        createImageSection(rightSection);

        mainContent.add(leftSection, rightSection);
        add(mainContent);

        setupInputListener();
    }

    private void createBackground() {
        String words = trieController.generateRandomText(600);

        Div bgText = new Div();
        bgText.addClassName("bg-text-content");
        bgText.setText(words + "  " + words);

        Div bgWall = new Div();
        bgWall.addClassName("bg-text-wall");
        bgWall.add(bgText);

        add(bgWall);
    }

    private void createHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.addClassName("header");
        header.setWidthFull();

        H1 title = new H1("FREDDY FAZBEAR'S TEXT PREDICTOR");
        title.addClassName("header-title");

        Span subtitle = new Span("SECURITY SYSTEM - NIGHT SHIFT");
        subtitle.addClassName("header-subtitle");

        VerticalLayout headerContent = new VerticalLayout(title, subtitle);
        headerContent.setPadding(false);
        headerContent.setSpacing(false);

        header.add(headerContent);
        add(header);
    }

    private void createImageSection(VerticalLayout container) {
        Div imageContainer = new Div();
        imageContainer.addClassName("image-section");

        Image image = new Image("fnaf4house.png", "FNAF Security Camera");
        image.addClassName("trie-image");
        image.setWidth("100%");

        imageContainer.add(image);
        container.add(imageContainer);
    }

    private void createInputSection(VerticalLayout container) {
        Div inputSection = new Div();
        inputSection.addClassName("input-section");

        H2 inputLabel = new H2("SECURITY INPUT");
        inputLabel.addClassName("section-title");

        inputField = new TextArea();
        inputField.setPlaceholder("ITS ME ITS ME ITS ME");
        inputField.setWidthFull();
        inputField.addClassName("input-field");
        inputField.setMinHeight("200px");
        inputField.setMaxHeight("400px");

        Button clearButton = new Button("CLEAR");
        clearButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        clearButton.addClassName("fnaf-button");
        clearButton.addClickListener(e -> {
            inputField.clear();
            wordAnalysis.removeAll();
            nextWordResults.removeAll();
            nextCharResults.removeAll();
        });

        wordAnalysis = new Div();
        wordAnalysis.addClassName("word-analysis");

        inputSection.add(inputLabel, inputField, clearButton, wordAnalysis);
        container.add(inputSection);
    }

    private void createPredictionsSection(VerticalLayout container) {
        Div predictionsContainer = new Div();
        predictionsContainer.addClassName("predictions-container");

        Div nextWordCard = new Div();
        nextWordCard.addClassName("prediction-card");

        H2 wordTitle = new H2("NEXT WORD DETECTED");
        wordTitle.addClassName("card-title");

        nextWordResults = new Div();
        nextWordResults.addClassName("results-area");

        nextWordCard.add(wordTitle, nextWordResults);

        Div nextCharCard = new Div();
        nextCharCard.addClassName("prediction-card");

        H2 charTitle = new H2("NEXT CHARACTER SCAN");
        charTitle.addClassName("card-title");

        nextCharResults = new Div();
        nextCharResults.addClassName("results-area");

        nextCharCard.add(charTitle, nextCharResults);

        predictionsContainer.add(nextWordCard, nextCharCard);
        container.add(predictionsContainer);
    }

    private void setupInputListener() {
        inputField.addValueChangeListener(event -> {
            String input = event.getValue();
            if (input == null || input.isEmpty()) {
                wordAnalysis.removeAll();
                nextWordResults.removeAll();
                nextCharResults.removeAll();
                return;
            }

            // word highlighting
            wordAnalysis.removeAll();
            for (String word : input.trim().toLowerCase().split("\\s+")) {
                if (word.isEmpty()) continue;
                Span s = new Span(word);
                s.addClassName(trieController.containsWord(word) ? "word-hit" : "word-miss");
                wordAnalysis.add(s);
                wordAnalysis.add(new com.vaadin.flow.component.Text(" "));
            }

            // predictions
            List<String> wordPredictions = trieController.getNextWordPredictions(input);
            List<Character> charPredictions = trieController.getNextCharPredictions(input);

            nextWordResults.removeAll();
            for (String word : wordPredictions) {
                Span badge = new Span(word);
                badge.addClassName("prediction-badge");
                badge.addClickListener(e -> inputField.setValue(inputField.getValue() + " " + word));
                nextWordResults.add(badge);
            }

            nextCharResults.removeAll();
            for (Character ch : charPredictions) {
                Span badge = new Span(String.valueOf(ch));
                badge.addClassName("prediction-badge");
                badge.addClickListener(e -> inputField.setValue(inputField.getValue() + ch));
                nextCharResults.add(badge);
            }
        });
        inputField.setValueChangeMode(ValueChangeMode.EAGER);
    }
}
