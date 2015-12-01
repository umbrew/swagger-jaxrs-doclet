package com.carma.swagger.doclet.sample.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@SuppressWarnings("javadoc")
@Path("project/{id: [0-9]+}")
public class RegexPathResource {

	@GET
	public String getById(@PathParam("id") int id) {
		System.out.println("get by id: " + id);
		return null;
	}
}
