# Ryan Shum - TDP Excercise 01 Submission

**Repository:** [criticalmass-aem](https://github.com/olmanslm/criticalmass-aem)

 **Note:** Uncomment section in Makefile to deploy if using Java 17.0.15
 -----

To test your completed exercise:

1. Switch to the main branch:
   ```
   git pull
   git checkout main
   ```

2. Build and deploy the code to your AEM instance:
   ```
   make author-fast
   ```

3. Navigate to an AEM page and add the RandomQuote component to test its functionality (http://localhost:4502/content/criticalmass/us/random-quote.html).

4. Within the test page, edit the RandomQuote component to view the available options:
   - **Quote Text**: Enter custom quote text
   - **Author**: Enter the quote author
   - **Your Quote**: Enter a personal/user quote that will be displayed separately

5. To test in-place editing:
   - Double-click directly on any user quote text displayed on the page
   - Edit the quote inline without opening the dialog
   - Save changes to see immediate updates

6. To test the loggers:
   - Open `/crx-quickstart/logs/my-custom-service-tdp.log` (TDP runmode logger)
   - Open `/crx-quickstart/logs/my-random-quote-service.log` (RandomQuoteService logger)
   - Reload the page with the component to see log entries

## Backend

The backend consists of the following:

- A RandomQuote component located at `ui.apps/src/main/content/jcr_root/apps/criticalmass/components/randomquote/`
- A Sling Model `RandomQuoteModel` at `core/src/main/java/com/criticalmass/core/models/RandomQuoteModel.java`
- OSGi Services:
  - `GreetingService` at `core/src/main/java/com/criticalmass/core/services/GreetingService.java`
  - `RandomQuoteService` at `core/src/main/java/com/criticalmass/core/services/RandomQuoteService.java`
  - Implementations in `core/src/main/java/com/criticalmass/core/services/impl/`
- OSGi Logger configurations:
  - `org.apache.sling.commons.log.LogManager.factory.config~tdp.cfg.json` (TDP runmode)
  - `org.apache.sling.commons.log.LogManager.factory.config~randomquoteservice.cfg.json` (API monitoring)
  - `org.apache.sling.commons.log.LogManager.factory.config~mycustomservice.cfg.json` (GreetingService)

## RandomQuote Component

The component fetches random inspirational quotes from https://thequoteshub.com/api/ and displays them with a three-tier precedence system:

1. **Dialog Properties**: Custom text and author entered by content authors
2. **Service Data**: Random quotes fetched from the external API
3. **Static Fallback**: Default quote when no dialog input or service data is available

### Enhanced Features

**User Quote Functionality**: The component now supports user-entered quotes with two editing approaches:
- **Dialog Editing**: Authors can enter personal quotes through the "Your Quote" field in the component dialog
- **In-Place Editing**: Direct editing of user quotes on the page by double-clicking the text

**Service Integration**: The component integrates with the GreetingService to demonstrate OSGi service injection and displays the greeting message alongside the quote content.

**Dual Display**: The component can simultaneously show both API/dialog quotes and user quotes, providing flexible content authoring options.
