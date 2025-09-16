# Mastering AEM Logging and Component Development: Hands-On Exercise

## Edgar Gonzalez - TDP Submission

To submit your completed exercise, follow these steps:

1. Switch to practice branch `feature/aem-custom-logger-exercise-edgar-gonzalez`:
    ```sh
    git pull
    git checkout feature/aem-custom-logger-exercise-edgar-gonzalez
    ```
2. Build and deploy the code to your AEM instance.
3. Install package `edgar-tdp-0.1.zip` using the AEM Package Manager.
4. Navigate to the AEM page `/content/criticalmass/us/en/exercise-01-edgar.html` to see the custom component in action. (http://localhost:4502/editor.html/content/criticalmass/us/en/exercise-01-edgar.html)
5. Within the test page, edit the component `Edgar Tdp Card Component` to view the available options and test its functionality.
    - Card Name: Enter a name look for using the magic the gathering api.
    - Show All Results: If checked, all results will be shown for cards that match in some way the provided 'Card Name'. If unchecked, only the first matching card will be shown.
8. To test the logger, open the AEM edgarTdpServiceImpl.log file located at `/crx-quickstart/logs/edgarTdpServiceImpl.log` and reload the test page. You should see log entries indicating the component's activity.
9. The component has some styles available in the style system. You can apply these styles to the component to see how it looks with different styling options.
    - Background Color: Choose a background color for the card.
    - Image Border: Choose a border style for the card image. You may select multiple options at once.

## Backend

The delivery has:

1. A component named `Edgar Tdp Card Component` located at `ui.apps/src/main/content/jcr_root/apps/criticalmass/components/edgarTdpCardComponent/edgarTdpCardComponent.html`.
2. A model class `EdgarTdpModel` located at `core/src/main/java/com/criticalmass/core/models/EdgarTdpModel.java`.
3. A Card model named `EdgarTdpCardsModel` located at `core/src/main/java/com/criticalmass/core/models/EdgarTdpCardsModel.java`
3. An OSGI Service `EdgarTdpService` located at `core/src/main/java/com/criticalmass/core/services/EdgarTdpService.java`, and implemented at `core/src/main/java/com/criticalmass/core/services/impl/EdgarTdpServiceImpl.java`
4. An OSGI Log configured at `ui.config/src/main/content/jcr_root/apps/criticalmass/osgiconfig/config/org.apache.sling.commons.log.LogManager.factory.config~criticalmassEdgarTdp.cfg.json`
5. A few additions to `all/pom.xml` and `core/pom.xml`.
6. An AEM package `edgar-tdp-0.1.zip` located at `results-edgar-gonzalez` with a test page ready to be used within the `Critical Mass Multi-Site Project`.
7. A Screenshot named `aem-log.png` showing the log entry after building and restarting AEM,  located at `results-edgar-gonzalez`.
8. A README.md file with instructions on how to test the component and logger.

## Edgar Tdp Card Component

The component is made to fetch data from a Magic the Gathering api, where it is set to run a request given a specific name we provide through the component's dialog, and it then displays the first and possibly all other matches to that given name.
The provided testing page has a few examples with different names, displaying only the first one, and one example where it pulls all available matches.

An easy way to find a list of card can be through a tcg e-comm site like https://starcitygames.com/shop/singles/, simply select a set and it will display a list of cards available in that given set.

Note: The API isn't fully up to date, so some cards may not be available.

---
