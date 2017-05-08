/**
    Copyright 2014-2015 Amazon.com, Inc. or its affiliates. All Rights Reserved.

    Licensed under the Apache License, Version 2.0 (the "License"). You may not use this file except in compliance with the License. A copy of the License is located at

        http://aws.amazon.com/apache2.0/

    or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package helloworld;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SessionEndedRequest;
import com.amazon.speech.speechlet.SessionStartedRequest;
import com.amazon.speech.speechlet.Speechlet;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SimpleCard;

/**
 * This sample shows how to create a simple speechlet for handling speechlet
 * requests.
 */

public class HelloWorldSpeechlet implements Speechlet {
	private static final Logger log = LoggerFactory.getLogger(HelloWorldSpeechlet.class);

	private long timeStart;
	private long timeDifference;
	private final String AVERAGE_PERSON_TIME = "8 minutes and 12 seconds";

	/**
	 * This is just to handle all of the timer file functions
	 */
	private ShowerTracker highscoreList;

	/**
	 * This is to diversify what the prompts will be a bit
	 */
	private ArrayList<String> InformationPrompt;
	private ArrayList<String> ReduceWaterFacts;

	@Override
	public void onSessionStarted(final SessionStartedRequest request, final Session session) throws SpeechletException {
		log.info("onSessionStarted requestId={}, sessionId={}", request.getRequestId(), session.getSessionId());

		InformationPrompt = new ArrayList<String>();
		ReduceWaterFacts = new ArrayList<String>();
		
		InformationPrompt.add("What information would you like to hear about?");
		InformationPrompt.add("What information did you want me to tell you about? ");
		InformationPrompt.add("What did you want me to tell you about?");
		InformationPrompt.add("What did you want to hear about?");
		
		ReduceWaterFacts.add("Purchase a new shower head. Older models can use up to 5 gallons per minute, which is "
				+ "twice the national average of 2.2 gallons per minute.");
		ReduceWaterFacts.add("If you are able to shower less often then it could make a difference in the long run"
				+ ", escpeially if you shower more than once per day.");
		ReduceWaterFacts.add("Turning off your sink while brushing your teeth can save a lot of water"
				+ " since the average faucet uses water at a rate of 2.5 gallons per minute.");
		ReduceWaterFacts.add("Check your home for water leaks. A small drip from a worn faucet can waste 20 gallons"
				+ " of water per day, and larger leaks can waste hundreds of gallons per day.");
		ReduceWaterFacts.add("Don't use your toilet to dispose of trash. Every time you flush a small piece of "
				+ "garbage, five to seven gallons are wasted.");
		ReduceWaterFacts.add("Insulate your water pipes so you don't have to waste water while waiting for it to heat"
				+ "up.");
		ReduceWaterFacts.add("Toilets use a lot of water per flush, and if you buy a low-flush toilet, it can save"
				+ "two to four gallons per flush");

		timeStart = 0;
		timeDifference = 0;
		highscoreList = new ShowerTracker();
	}

	@Override
	public SpeechletResponse onLaunch(final LaunchRequest request, final Session session) throws SpeechletException {
		log.info("onLaunch requestId={}, sessionId={}", request.getRequestId(), session.getSessionId());
		return getWelcomeResponse();
	}

	@Override
	public SpeechletResponse onIntent(final IntentRequest request, final Session session) throws SpeechletException {
		log.info("onIntent requestId={}, sessionId={}", request.getRequestId(), session.getSessionId());

		Intent intent = request.getIntent();
		String intentName = (intent != null) ? intent.getName() : null;

		if ("StartTimerIntent".equals(intentName)) {
			return getStartTimerResponse();
		} else if ("EndTimerIntent".equals(intentName)) {
			return getEndTimerResponse();
		} else if ("InformationIntent".equals(intentName)) {
			return getInformationResponse(intent, session);
		} else if ("AMAZON.HelpIntent".equals(intentName)) {
			return getHelpResponse();
		} else {
			throw new SpeechletException("Invalid Intent");
		}
	}

	@Override
	public void onSessionEnded(final SessionEndedRequest request, final Session session) throws SpeechletException {
		log.info("onSessionEnded requestId={}, sessionId={}", request.getRequestId(), session.getSessionId());
		// any cleanup logic goes here
	}

	/**
	 * Creates and returns a {@code SpeechletResponse} with a welcome message.
	 *
	 * @return SpeechletResponse spoken and visual response for the given intent
	 */
	private SpeechletResponse getWelcomeResponse() {
		String speechText = "Welcome to Power Shower";

		// Create the Simple card content.
		SimpleCard card = new SimpleCard();
		card.setTitle("PowerShowerStart");
		card.setContent(speechText);

		// Create the plain text output.
		PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
		speech.setText(speechText);

		// Create reprompt
		Reprompt reprompt = new Reprompt();
		reprompt.setOutputSpeech(speech);

		return SpeechletResponse.newAskResponse(speech, reprompt, card);
	}

	/**
	 * Creates and returns a {@code SpeechletResponse} with an ending message.
	 *
	 * @return SpeechletResponse spoken and visual response for the given intent
	 */
	private SpeechletResponse getEndTimerResponse() {

		timeDifference = (System.nanoTime() - timeStart) / 1000000000;
		highscoreList = new ShowerTracker();

		log.debug(Long.toString(highscoreList.getBestTime()));
		highscoreList.writeToFile(Long.toString(timeDifference));
		long oldHighscore = highscoreList.getBestTime();
		log.debug(Long.toString(highscoreList.getBestTime()));
		String speechText = String.valueOf(oldHighscore);
//		if (oldHighscore > timeDifference) {
//			speechText = "The timer has ended. You took " + timeDifference + " seconds to finish showering.";
//		} else if (oldHighscore == timeDifference) {
//			speechText = "The timer has ended. You took " + timeDifference + " seconds to finish showering. "
//					+ "You just tied your shortest shower recorded!";
//		} else {
//			speechText = "The timer has ended. You took " + timeDifference + " seconds to finish showering. "
//					+ "You just set a new shortest shower!";
//		}

		// Create the Simple card content.
		SimpleCard card = new SimpleCard();
		card.setTitle("PowerShowerEnd");
		card.setContent(speechText);

		// Create the plain text output.
		PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
		speech.setText(speechText);

		// Create reprompt
		Reprompt reprompt = new Reprompt();
		reprompt.setOutputSpeech(speech);

		return SpeechletResponse.newTellResponse(speech, card);
	}

	/**
	 * Creates a {@code SpeechletResponse} for the start timer intent.
	 *
	 * @return SpeechletResponse spoken and visual response for the given intent
	 */
	private SpeechletResponse getStartTimerResponse() {
		String speechText = "Starting Timer. Ready, <break time=\"0.1s\" /> Set, <break time=\"0.1s\" /> Go!";

		timeStart = System.nanoTime();

		// Create the Simple card content.
		SimpleCard card = new SimpleCard();
		card.setTitle("StartTimer");
		card.setContent(speechText);

		// Create the plain text output.
		PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
		speech.setText(speechText);
		// Create reprompt
		Reprompt reprompt = new Reprompt();
		reprompt.setOutputSpeech(speech);

		return SpeechletResponse.newAskResponse(speech, reprompt, card);
	}

	/**
	 * Creates a {@code SpeechletResponse} for the Information intent.
	 *
	 * @return SpeechletResponse spoken and visual response for the given intent
	 */
	private SpeechletResponse getInformationResponse(final Intent intent, final Session session) {

		String speechText = "Nothing yet";
		
		if(intent.getSlot("Times").getValue() != ""){
			// This line will make the prompt not as boring in case the user uses it
			// multiple times
			int averageTime = highscoreList.getAverageTime();
			speechText = "Your average shower time is: " + averageTime + " seconds long. Compare that to the national "
					+ "average of " + AVERAGE_PERSON_TIME;
					
		}else if(intent.getSlot("Reduce").getValue() != null){
			speechText = ReduceWaterFacts.get((int) (Math.random() * (ReduceWaterFacts.size() - 1)));
		}else{
			speechText = InformationPrompt.get((int) (Math.random() * (InformationPrompt.size() - 1)))
					+ " Choose between my showering times,"
					+ "or strategies to reduce my water usage,";
		}
		
		

		// Create the Simple card content.
		SimpleCard card = new SimpleCard();
		card.setTitle("Information");
		card.setContent(speechText);

		// Create the plain text output.
		PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
		speech.setText(speechText);
		// Create reprompt
		Reprompt reprompt = new Reprompt();
		reprompt.setOutputSpeech(speech);

		return SpeechletResponse.newAskResponse(speech, reprompt, card);
	}

	/**
	 * Creates a {@code SpeechletResponse} for the help intent.
	 *
	 * @return SpeechletResponse spoken and visual response for the given intent
	 */
	private SpeechletResponse getHelpResponse() {
		String speechText = "This application will help reduce your water usage in showers."
				+ " Ask Power Shower to start the timer in order to time your shower,"
				+ "and when you're done, ask Power Shower to stop the timer "
				+ "Information about your showers will be locally recorded so that you can"
				+ "recieve information about your showering habits. Just ask Power Shower about"
				+ "your showering habits to hear more.";

		// Create the Simple card content.
		SimpleCard card = new SimpleCard();
		card.setTitle("PowerShowerHelp");
		card.setContent(speechText);

		// Create the plain text output.
		PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
		speech.setText(speechText);

		// Create reprompt
		Reprompt reprompt = new Reprompt();
		reprompt.setOutputSpeech(speech);

		return SpeechletResponse.newAskResponse(speech, reprompt, card);
	}
}
