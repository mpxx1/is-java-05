package me.macao.percistence;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import me.macao.dto.DetailsCreateDTO;
import me.macao.dto.DetailsUpdateDTO;
import me.macao.exception.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDaoImpl
    implements UserDao {

    @Autowired
    private JpaUserDao userDao;

    @Override
    public Optional<User> findByEmail(final String email) {

        return userDao
                .findAll()
                .stream()
                .filter(x -> x.getUsername().equals(email))
                .findFirst();
    }

    @Override
    public Collection<User> findAll() {

        return userDao.findAll();
    }

    @Override
    public User findById(Long id)
            throws ObjectNotFoundException {

        return userDao
                .findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("User with id " + id + " not found"));
    }

    @Override
    public User create(final DetailsCreateDTO userDto) {

        return userDao
                .save(
                        User
                                .builder()
                                .username(userDto.username())
                                .password(userDto.pass_hash())
                                .salt(userDto.salt())
                                .role(userDto.role())
                                .build()
                );
    }

    @Override
    @Transactional
    public User update(final DetailsUpdateDTO userDto)
            throws ObjectNotFoundException {

        User user = findById(userDto.id());

        user.setPassword(userDto.new_pass_hash());
        user.setSalt(userDto.new_salt());

        return user;
    }

    @Override
    public void deleteById(final Long id) {

        userDao.deleteById(id);
    }
}
