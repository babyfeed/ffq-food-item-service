package edu.fiu.ffqr.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

import edu.fiu.ffqr.models.TrackerResult;
import edu.fiu.ffqr.service.TrackerResultsService;

import org.bson.types.ObjectId;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/tracker")
public class TrackerResultsController {
	
	@Autowired
	private TrackerResultsService trackerResultsService;
	
	public TrackerResultsController() {}

	@GetMapping("/all")
	public List<TrackerResult> getAllResults() throws JsonProcessingException {
		List<TrackerResult> results = trackerResultsService.getAll();
		return results;
	}

	@GetMapping("/user/{userID}")
	public List<TrackerResult> getResultsByUserId(@PathVariable("userID") String userId) throws JsonProcessingException {
		List<TrackerResult> results = trackerResultsService.getResultsByUserId(userId);
		return results;
	}

	@PostMapping("")
	public TrackerResult create(@RequestBody TrackerResult data) throws JsonProcessingException {
		TrackerResult result = trackerResultsService.create(data);
		return result;
	}

	@PutMapping("/tracker/update")
	public TrackerResult update(@RequestBody TrackerResult data) throws JsonProcessingException {

			String id = data.getId();

			if (null == id) {
			throw new IllegalArgumentException("Missing Tracker Result ID");
			}
		
			TrackerResult trItem = trackerResultsService.findById(id);
			if (null == trItem) {
			throw new IllegalArgumentException("Invalid Tracker Result ID");
			}
        if (data.getGoal() != null) {
            trItem.setGoal(data.getGoal());
        }

        trackerResultsService.update(trItem);

        return trItem;
    }
}
