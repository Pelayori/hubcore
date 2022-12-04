package os.arcadiadevs.playerservers.hubcore.controllers;

import java.util.List;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import os.arcadiadevs.playerservers.hubcore.models.Server;

/**
 * A class that handles all the database operations for the servers object.
 *
 * @author ArcadiaDevs
 */
public class ServersController {

  private final SessionFactory sessionFactory;

  /**
   * Creates a new instance of the servers controller.
   *
   * @param sessionFactory The session factory
   */
  public ServersController(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  /**
   * Create a new server in the database.
   *
   * @param server The server to create
   */
  public void createServer(Server server) {
    Session session = this.sessionFactory.getCurrentSession();
    session.beginTransaction();
    session.persist(server);
    session.getTransaction().commit();
  }

  /**
   * Gets all the servers from the database.
   *
   * @return A list of all the servers
   */
  public List<Server> getServers() {
    Session session = this.sessionFactory.getCurrentSession();
    session.beginTransaction();
    List<Server> servers = session.createQuery("from Server", Server.class).list();
    session.getTransaction().commit();
    return servers;
  }

  /**
   * Gets the server by the server id.
   *
   * @param ownerId The owner id
   * @return The server
   */
  public Server getServer(UUID ownerId) {
    Session session = this.sessionFactory.getCurrentSession();
    session.beginTransaction();
    Server server = session
        .createQuery("from Server where owner = :owner", Server.class)
        .setParameter("owner", ownerId)
        .uniqueResult();
    session.getTransaction().commit();
    return server;
  }

  public Server getServer(Player player) {
    return getServer(player.getUniqueId());
  }

  /**
   * Checks if the player has a server.
   *
   * @param owner The player to check
   * @return True if the player has a server, false otherwise
   */
  public boolean hasServer(UUID owner) {
    Session session = this.sessionFactory.getCurrentSession();
    session.beginTransaction();
    Server server = session
        .createQuery("from Server where owner = :owner", Server.class)
        .setParameter("owner", owner)
        .uniqueResult();
    session.getTransaction().commit();
    return server != null;
  }

  public boolean hasServer(Player player) {
    return hasServer(player.getUniqueId());
  }

  /**
   * Checks if a server exists by the server id.
   *
   * @param id The server id
   * @return True if the server exists, false otherwise
   */
  public boolean serverExists(String id) {
    Session session = this.sessionFactory.getCurrentSession();
    session.beginTransaction();
    Server server = session
        .createQuery("from Server where id = :id", Server.class)
        .setParameter("id", id)
        .uniqueResult();
    session.getTransaction().commit();
    return server != null;
  }

  /**
   * Renames the server.
   *
   * @param owner The owner of the server
   * @param id    The new id (name)
   */
  public void renameServer(UUID owner, String id) {
    Session session = this.sessionFactory.getCurrentSession();
    session.beginTransaction();
    Server server = session
        .createQuery("from Server where owner = :owner", Server.class)
        .setParameter("owner", owner)
        .uniqueResult();
    server.setId(id);
    session.getTransaction().commit();
  }

  public void renameServer(Player player, String id) {
    renameServer(player.getUniqueId(), id);
  }

  /**
   * Deletes the server.
   *
   * @param owner The owner of the server
   */
  public void removePlayer(UUID owner) {
    Session session = this.sessionFactory.getCurrentSession();
    session.beginTransaction();
    Server server = session
        .createQuery("from Server where owner = :owner", Server.class)
        .setParameter("owner", owner)
        .uniqueResult();
    session.remove(server);
    session.getTransaction().commit();
  }

  public void removePlayer(Player player) {
    removePlayer(player.getUniqueId());
  }
}
