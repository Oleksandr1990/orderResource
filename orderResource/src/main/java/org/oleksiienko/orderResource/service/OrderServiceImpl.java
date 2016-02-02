/* This is a part of my freelance project. For some reasons I haven't published security, user and product classes.

 * OrderService provides order validation and forwards request to orderDao which connects to DB.
 * JMSMessenger sends a message to listener which sends e-mail to customer and connects to 1C (COM connection) 
 * 
 *  Oleksandr Oleksiienko
 *  oleksandr.oleksiienko90@gmail.com
 */

package org.oleksiienko.orderResource.service;

import java.util.Date;
import java.util.List;

import org.oleksiienko.orderResource.dao.OrderDao;
import org.oleksiienko.orderResource.dao.OrderDaoImpl;
import org.oleksiienko.orderResource.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.oleksiienko.orderResource.jms.JMSMessenger;




@Service
public class OrderServiceImpl implements OrderService {
	@Autowired
	private OrderDao orderDao;

	
	@Autowired
	private JMSMessenger jmsMessenger;
	
	@Transactional
	@Override
	public Order add(Order order){
		//check if order already exists
    	Order existingOrder = orderDao.get(order.getId());
    	
    	if(existingOrder!=null || !validateOrder(order))
			return null;
		else {
    		orderDao.add(order);
    		//connect to 1c and send e-mail to user
    		if(order!=null){
    			jmsMessenger.sendMessage(order.getId(), "create");
    		}
    		return order;
		}
	}
	@Transactional
	@Override
	public Order get(long orderId, String userName){
		return orderDao.getOrderByIdAndName(orderId, userName);
	}
	
	@Transactional
	@Override
	public List<Order> getOrderList(String userName, Date from, Date to, String orderBy, String sortOrder){
		return orderDao.getOrderList(userName, from, to, orderBy, sortOrder);
	}
	
	/*
	 @Transactional
	public Order delete(Order order) {
		
		return orderDao.delete(order);
	}
	  
	 */
	
	
	
	@Transactional
	@Override
	public Order delete(long id) {
		Order o = orderDao.delete(id);
		if(o==null){
    		jmsMessenger.sendMessage(o.getId(), "delete");
    	}
		return o;
	}
	@Transactional
	@Override
	public Order edit(Order order) {
		if(validateOrder(order)){
			orderDao.update(order);
			jmsMessenger.sendMessage(order.getId(), "edit");
			return order;
		}
		else {
			return null;
		}
	}
	
	private boolean validateOrder(Order order){
		/*
		 * Order validation  
		 * (if order has products, if !null Customer etc. 
		 */
		return true;
	}
}
