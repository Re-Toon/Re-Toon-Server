package retoon.retoon_server.src.webtoon;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import retoon.retoon_server.src.webtoon.model.GetWebtoonListGenreReq;
import retoon.retoon_server.src.webtoon.model.GetWebtoonListGenreRes;

import javax.sql.DataSource;

@Repository
public class WebtoonDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


}
