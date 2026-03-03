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
    
    public MainView(TrieController trieController) {
        this.trieController = trieController;
        
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        addClassName("main-container");
        
        createHeader();
        
        // Create main content area with horizontal layout
        HorizontalLayout mainContent = new HorizontalLayout();
        mainContent.setWidthFull();
        mainContent.setMaxWidth("1400px");
        mainContent.getStyle()
            .set("margin", "0 auto")
            .set("display", "flex")
            .set("flex-direction", "row");
        mainContent.setPadding(true);
        mainContent.setSpacing(true);
        mainContent.setAlignItems(com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment.START);
        
        // Left side - input and predictions
        VerticalLayout leftSection = new VerticalLayout();
        leftSection.addClassName("left-section");
        leftSection.getStyle()
            .set("flex", "1 1 auto")
            .set("min-width", "0");
        leftSection.setPadding(false);
        leftSection.setSpacing(true);
        
        createInputSection(leftSection);
        createPredictionsSection(leftSection);
        
        // Right side - image
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
        
        // Image is in: src/main/resources/META-INF/resources/fnaf4house.png
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
        
        // Create TextArea for larger text input
        inputField = new TextArea();
        inputField.setPlaceholder("ITS ME ITS ME ITS ME");
        inputField.setWidthFull();
        inputField.addClassName("input-field");
        inputField.setMinHeight("200px");
        inputField.setMaxHeight("400px");
        
        Button clearButton = new Button("CLEAR");
        clearButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        clearButton.addClassName("fnaf-button");
        clearButton.addClickListener(e -> handleClear());
        
        inputSection.add(inputLabel, inputField, clearButton);
        container.add(inputSection);
    }
    
    private void createPredictionsSection(VerticalLayout container) {
        Div predictionsContainer = new Div();
        predictionsContainer.addClassName("predictions-container");
        
        // Next Word Predictions Card
        Div nextWordCard = new Div();
        nextWordCard.addClassName("prediction-card");
        
        H2 wordTitle = new H2("NEXT WORD DETECTED");
        wordTitle.addClassName("card-title");
        
        nextWordResults = new Div();
        nextWordResults.addClassName("results-area");
        
        nextWordCard.add(wordTitle, nextWordResults);
        
        // Next Character Predictions Card
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
            if (input != null && !input.isEmpty()) {
                updatePredictions(input);
            } else {
                clearPredictions();
            }
        });
        
        // Ensure it updates on every keystroke, not just on blur/enter
        inputField.setValueChangeMode(com.vaadin.flow.data.value.ValueChangeMode.EAGER);
    }
    
    private void updatePredictions(String input) {
        // Call controller methods to get predictions
        List<String> wordPredictions = trieController.getNextWordPredictions(input);
        List<Character> charPredictions = trieController.getNextCharPredictions(input);
        
        // Display the results
        displayWordPredictions(wordPredictions);
        displayCharPredictions(charPredictions);
    }
    
    private void displayWordPredictions(List<String> predictions) {
        nextWordResults.removeAll();
        for (String word : predictions) {
            Span badge = createPredictionBadge(word);
            badge.addClickListener(e -> inputField.setValue(inputField.getValue() + " " + word));
            nextWordResults.add(badge);
        }
    }
    
    private void displayCharPredictions(List<Character> predictions) {
        nextCharResults.removeAll();
        for (Character ch : predictions) {
            Span badge = createPredictionBadge(String.valueOf(ch));
            badge.addClickListener(e -> inputField.setValue(inputField.getValue() + ch));
            nextCharResults.add(badge);
        }
    }
    
    private Span createPredictionBadge(String text) {
        Span badge = new Span(text);
        badge.addClassName("prediction-badge");
        return badge;
    }
    
    private void handleClear() {
        inputField.clear();
        clearPredictions();
    }
    
    private void clearPredictions() {
        nextWordResults.removeAll();
        nextCharResults.removeAll();
    }
}