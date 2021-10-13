import Mappers.AccountMapper;
import Mappers.MessagesMapper;
import Resources.AccountResource;
import Resources.MessagesResource;
import com.github.arteam.jdbi3.JdbiFactory;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.eclipse.jetty.server.session.SessionHandler;
import org.jdbi.v3.core.Jdbi;

public class ApplicationClass extends Application<ConfigClass> {

        public static void main(final String[] args) throws Exception {
            new ApplicationClass().run(args);
        }

        @Override
        public void initialize(final Bootstrap<ConfigClass> bootstrap) {
        }

        @Override
        public void run(final ConfigClass configClass, final Environment environment) throws Exception {

            final JdbiFactory factory = new JdbiFactory();
            final Jdbi db = factory.build(environment, configClass.getDataSourceFactory(), "mysql");
            db.registerRowMapper(new AccountMapper());
            db.registerRowMapper(new MessagesMapper());

            final AccountResource res = new AccountResource(db); //initialize. allow requests.
            final MessagesResource mes = new MessagesResource(db); //initialize. allow requests.


            environment.servlets().setSessionHandler(new SessionHandler());
            environment.jersey().register(res);
            environment.jersey().register(mes);

        }
    }