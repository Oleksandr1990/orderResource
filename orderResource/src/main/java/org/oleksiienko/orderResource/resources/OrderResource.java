/* This is a part of my freelance project. For some reasons I haven't published security, user and product classes.
 * Order resource provides CRUD operations to service. 
 * Security check uses injected User authenticatedUser to check if user from request and authenticated user is a same person.
 * OrderService provides order validation and forwards request to orderDao which connects to DB.
 * 
 *  Oleksandr Oleksiienko
 *  oleksandr.oleksiienko90@gmail.com
 */

package org.oleksiienko.orderResource.resources;

import java.net.URI;
import java.util.Date;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.oleksiienko.orderResource.model.ListItem;
import org.oleksiienko.orderResource.model.Order;
import org.oleksiienko.orderResource.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;





@Path("orders")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Controller
public class OrderResource {
	
	   //@Inject
	   // @AuthenticatedUser
	    //User authenticatedUser;
	
	@Autowired
	OrderService orderService;
    @POST
  //@Secured(Role.customer, Role.manager)
    public Response addOrder(Order order, @Context UriInfo uriInfo) {
    	//Check if customer from JSON is an AuthenticatedUser user 


    	/*
    	if(authenticatedUser.getName().equals(order.getCustomer().getLogin())
    		{
    	*/
    		Order newOrder = orderService.add(order);    
    		if(newOrder!=null){
    			//set link for added order
    			newOrder.setLink(getUriForSelf(uriInfo, order));
    			
    			//set link for profile
    			newOrder.getCustomer().setLink(getUriForProfile(uriInfo, newOrder.getCustomer().getLogin()));
    			
    			// set links for products
    			setUriForProducts(uriInfo, newOrder.getListItems());
    	
    			//build response
    			URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(newOrder.getOrderId())).build();
    			return Response.created(uri)
    					.entity(newOrder)
    					.build();
    		} else 
    			return Response.status(Status.BAD_REQUEST).build();
    	/*} else {
			//Wrong user to edit order
			return Response.status(Status.FORBIDDEN);
    	}*/
    }
    @GET
    public Response getOrderList(@Context UriInfo uriInfo, @QueryParam("userName") String userName, @QueryParam("from") Date from, @QueryParam("to") Date to, @QueryParam("orderBy") String orderBy, @QueryParam("sortOrder") String sortOrder){
    	//Check if customer from JSON is an AuthenticatedUser user 

    	/*

        if(authenticatedUser.getName().equals(userName))
    		{
    	*/
    		List<Order> orderList = orderService.getOrderList(userName, from, to, orderBy, sortOrder);
    		if(orderList==null || orderList.size()==0)
    			return Response.status(Status.NOT_FOUND)
    					.build();
    		else {
    			for(Order order:orderList){
    				order.setLink(getUriForSelf(uriInfo, order));
    				//set link for profile
    				order.getCustomer().setLink(getUriForProfile(uriInfo, order.getCustomer().getLogin()));
    				setUriForProducts(uriInfo, order.getListItems());
    			}
    			
    			return Response.status(Status.FOUND)
    					.entity(orderList)
    					.build();
    		}
    	/*} else {
		//Wrong user to edit order
		return Response.status(Status.FORBIDDEN);
	}*/
    }
    @GET
    @Path("/{orderId}")
    public Response getOrder(@PathParam("orderId") long orderId, @QueryParam("userName") String userName, @Context UriInfo uriInfo)  {
    	//Check if customer from JSON is an AuthenticatedUser user 

    	/*
    	if(authenticatedUser.getName().equals(userName)
    		{
    	*/
    	
			Order order = orderService.get(orderId, userName);
			
			if(order==null)
				return Response.status(Status.NOT_FOUND).build();
			
			order.setLink(getUriForSelf(uriInfo, order));
			//set link for profile
			order.getCustomer().setLink(getUriForProfile(uriInfo, order.getCustomer().getLogin()));
			
			setUriForProducts(uriInfo, order.getListItems());
			
			
			
			return Response.status(Status.FOUND)
					.entity(order)
					.build();    
			/*} else {
			//Wrong user to edit order
			return Response.status(Status.FORBIDDEN);
    	}*/
    }
       
    @PUT
    @Path("/{orderId}")
    public Response updateOrder(@PathParam("orderId") long id, Order order, @Context UriInfo uriInfo){
    	//Check if customer from JSON is an AuthenticatedUser user 
    	
    	/*
    	if(authenticatedUser.getName().equals(order.getCustomer().getLogin())
    		{
    	*/
    	
    			Order editedOrder = orderService.edit(order);
    			if(editedOrder==null)
    				return Response.status(Status.BAD_REQUEST).build();
    			editedOrder.setLink(getUriForSelf(uriInfo, editedOrder));
    			setUriForProducts(uriInfo, editedOrder.getListItems());
    			//set link for profile
    			editedOrder.getCustomer().setLink(getUriForProfile(uriInfo,editedOrder.getCustomer().getLogin()));
    			
    			return Response.ok()
    					.entity(editedOrder)
    					.build();    
    	/*} else {
    		//Wrong user to edit order
    		return Response.status(Status.FORBIDDEN);
        }*/
    }
   
	@DELETE
	@Path("/{orderId}")
	public Response deleteMessage(@PathParam("orderId") long id) {
		//Check if customer from JSON is an AuthenticatedUser user 
			
		/*
		 Order order = orderService.get(id);
		 
    	if(authenticatedUser.getName().equals(order.getCustomer().getLogin())
    		{
				Order deletedOrder = orderService.delete(order);
		*/
				Order deletedOrder = orderService.delete(id);
				if(deletedOrder!=null)
					return Response.status(Status.BAD_REQUEST).build();
				
				return Response.ok().build();
		/*} else {
	    	//Wrong user to edit order
	    	return Response.status(Status.FORBIDDEN);
	      }*/
	}
    
    
    private String getUriForSelf(UriInfo uriInfo, Order order){
    	String uri = uriInfo.getBaseUriBuilder()
    		.path(OrderResource.class)
    		.path(Long.toString(order.getOrderId()))
    		.queryParam("userName", order.getCustomer().getLogin())
    		.build()
    		.toString();
    	return uri;
    }
   
    private String getUriForProfile(UriInfo uriInfo, String login){
    	String uri = uriInfo.getBaseUriBuilder()
    			.path(ProfileResource.class)
    			.path(login)
    			.build()
    			.toString();
    	return uri;
    }
    
    private void setUriForProducts(UriInfo uriInfo, List<ListItem> productList){
    	for(ListItem item:productList){
    		String uri = uriInfo.getBaseUriBuilder()
    				.path(ProductResource.class)
    				.path(item.getProduct().getUserFriendlyName())
    				.build()
    				.toString();
    		item.getProduct().setLink(uri);
    		
    	}
    }
}
