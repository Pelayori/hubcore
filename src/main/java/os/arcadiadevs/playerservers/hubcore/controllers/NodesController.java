package os.arcadiadevs.playerservers.hubcore.controllers;

import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import os.arcadiadevs.playerservers.hubcore.models.Node;

/**
 * A class that handles all the database operations for node object.
 *
 * @author ArcadiaDevs
 */
public class NodesController {

  private final SessionFactory sessionFactory;

  public NodesController(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  /**
   * Create a new node in the database.
   *
   * @param node The node to create
   */
  public void createNode(Node node) {
    try (Session session = sessionFactory.openSession()) {
      session.beginTransaction();
      session.persist(node);
      session.getTransaction().commit();
    }
  }

  /**
   * Gets all the nodes from the database.
   *
   * @return A list of all the nodes
   */
  public List<Node> getNodes() {
    try (Session session = sessionFactory.openSession()) {
      return session.createQuery("from Node", Node.class).list();
    }
  }

  /**
   * Gets the next available node.
   *
   * @return The next available node
   */
  public Node getNextNode() {
    List<Node> nodes = getNodes();

    if (nodes.isEmpty()) {
      return null;
    }

    Node emptiestNode = nodes.get(0);

    for (Node node : nodes) {
      if (node.getServers().size() < emptiestNode.getServers().size()) {
        emptiestNode = node;
      }
    }

    return emptiestNode;
  }
}
