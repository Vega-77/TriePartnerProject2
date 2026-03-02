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
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import java.util.List;

@Route("")
@PageTitle("Trie Word Predictor")
public class MainView extends VerticalLayout {
    
    private final TrieController trieController;
    
    private TextField inputField;
    private Div nextWordResults;
    private Div nextCharResults;
    
    public MainView(TrieController trieController) {
        this.trieController = trieController;
        
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        addClassName("main-container");
        
        createHeader();
        
        VerticalLayout contentArea = new VerticalLayout();
        contentArea.addClassName("content-area");
        contentArea.setWidthFull();
        contentArea.setMaxWidth("1200px");
        contentArea.setPadding(true);
        
        createImageSection(contentArea);
        createInputSection(contentArea);
        createPredictionsSection(contentArea);
        
        add(contentArea);
        
        setupInputListener();
    }
    
    private void createHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.addClassName("header");
        header.setWidthFull();
        
        H1 title = new H1("Trie Word Predictor");
        title.addClassName("header-title");
        
        header.add(title);
        add(header);
    }
    
    private void createImageSection(VerticalLayout container) {
        Div imageContainer = new Div();
        imageContainer.addClassName("image-section");
        
        // Replace with your actual image path
        Image image = new Image("images/trie-diagram.png", "Trie visualization");
        image.addClassName("trie-image");
        
        imageContainer.add(image);
        container.add(imageContainer);
    }
    
    private void createInputSection(VerticalLayout container) {
        Div inputSection = new Div();
        inputSection.addClassName("input-section");
        
        H2 inputLabel = new H2("Enter Text");
        inputLabel.addClassName("section-title");
        
        inputField = new TextField();
        inputField.setPlaceholder("Start typing to get predictions...");
        inputField.setWidthFull();
        inputField.addClassName("input-field");
        
        Button clearButton = new Button("Clear");
        clearButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
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
        
        H2 wordTitle = new H2("Next Word Predictions");
        wordTitle.addClassName("card-title");
        
        nextWordResults = new Div();
        nextWordResults.addClassName("results-area");
        
        nextWordCard.add(wordTitle, nextWordResults);
        
        // Next Character Predictions Card
        Div nextCharCard = new Div();
        nextCharCard.addClassName("prediction-card");
        
        H2 charTitle = new H2("Next Character Predictions");
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
    
    private void displayWordPredictions(java.util.List<String> predictions) {
        nextWordResults.removeAll();
        for (String word : predictions) {
            Span badge = createPredictionBadge(word);
            badge.addClickListener(e -> inputField.setValue(inputField.getValue() + " " + word));
            nextWordResults.add(badge);
        }
    }
    
    private void displayCharPredictions(java.util.List<Character> predictions) {
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