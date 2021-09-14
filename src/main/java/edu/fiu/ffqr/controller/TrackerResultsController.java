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

	@PutMapping("/update/{id}")
	public TrackerResult updateGoal(@PathVariable ObjectId id, @RequestBody TrackerResult data) throws JsonProcessingException {
		
        TrackerResult trItem = trackerResultsService.findById(id);

        if (null == trItem) {
            throw new IllegalArgumentException("The tracker result id does not exist");
        }

        if (data.getUserId() != null) {
            trItem.setUserId(data.getUserId());
        }

        if (data.getAge() != null) {
            fdItem.setAge(data.getAge());
        }

        if (data.getResponses() != null) {
            fdItem.setResponses(data.getResponses());
        }

        if (data.getGoal() != null) {
            fdItem.setGoal(data.getGoal());
        }

        trackerResultsService.update(trItem);

        return trItem;
    }
}
