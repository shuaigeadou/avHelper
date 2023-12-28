package org.aver.avHelper.controller;

import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

@RestController()
@RequestMapping("/manget-cache")
public class MangetCacheController {
	private ConcurrentLinkedQueue<String> mangets = new ConcurrentLinkedQueue<>();

	@GetMapping("/removeManget")
	public String removeManget() {
		return mangets.poll();
	}
	
	@GetMapping("/getManget")
	public String getManget() {
		return mangets.peek();
	}
	
	@PostMapping("/addManget")
	public boolean addManget(@RequestBody Map<String,String> map) {
		return mangets.add(map.get("manget"));
	}
}
