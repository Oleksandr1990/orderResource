# orderResource
Part of my freelance project

In this project I developed an OrderResource using Jersey.
Also I developed an Order Service which forwards request operations to order DAO and sends message via JMS Messenger.
JMS Listener retrievs order from DB(it gets param orderID) and forwards order object to 1C util and E-mail util.
Order dao provides CRUD operations with order.

