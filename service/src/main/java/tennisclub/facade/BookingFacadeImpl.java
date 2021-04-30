package tennisclub.facade;

import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tennisclub.dto.booking.BookingCreateDTO;
import tennisclub.dto.booking.BookingFullDTO;
import tennisclub.dto.lesson.LessonFullDTO;
import tennisclub.dto.user.UserDTO;
import tennisclub.entity.Booking;
import tennisclub.entity.Court;
import tennisclub.entity.User;
import tennisclub.service.BookingService;
import tennisclub.service.CourtService;
import tennisclub.service.TimeService;
import tennisclub.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Miroslav Demek
 */
@Service
@Transactional
public class BookingFacadeImpl implements BookingFacade {

    private final  BookingService bookingService;
    private final CourtService courtService;
    private final UserService userService;

    final private Mapper mapper;

    @Autowired
    public BookingFacadeImpl(BookingService bookingService,
                             CourtService courtService,
                             UserService userService,
                             Mapper mapper) {
        this.bookingService = bookingService;
        this.courtService = courtService;
        this.userService = userService;
        this.mapper = mapper;
    }

    @Override
    public Long makeBooking(BookingCreateDTO createDTO) {
        Booking booking = mapper.map(createDTO, Booking.class);

        if (!courtService.isFree(booking.getCourt(), booking.getStartTime(), booking.getEndTime())) {
            throw new SecurityException("Can't make a booking. Court is not free at this time.");
        }

        /*
        for (User user : booking.getUsers()) {
            if (!userService.isFree(user, booking.getStartTime(), booking.getEndTime())) {
                throw new SecurityException("Can't make a booking. User is busy at this time.");
            }
        }
        */

        Booking newBooking = bookingService.create(booking);

        return newBooking.getId();
    }

    @Override
    public void cancelBooking(Long bookingId) {
        Booking booking = bookingService.findById(bookingId);
        bookingService.remove(booking);
    }

    @Override
    public BookingFullDTO addUser(Long bookingId, Long userId) {
        Booking booking = bookingService.findById(bookingId);
        User user = userService.findUserById(userId);
        Booking updated = bookingService.addUser(booking, user);
        return mapper.map(updated, BookingFullDTO.class);
    }

    @Override
    public BookingFullDTO removeUser(Long bookingId, Long userId) {
        Booking booking = bookingService.findById(bookingId);
        User user = userService.findUserById(userId);
        Booking updated = bookingService.removeUser(booking, user);
        return mapper.map(updated, BookingFullDTO.class);
    }

    @Override
    public List<BookingFullDTO> findAll() {
        List<Booking> bookings = bookingService.findAll();
        return bookings.stream()
                .map(b -> mapper.map(b, BookingFullDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public BookingFullDTO findById(Long bookingId) {
        Booking booking = bookingService.findById(bookingId);
        return mapper.map(booking, BookingFullDTO.class);
    }

    @Override
    public List<BookingFullDTO> findByTimeInterval(LocalDateTime from, LocalDateTime to) {
        List<Booking> bookings = bookingService.findByTimeInterval(from ,to);
        return bookings.stream()
                .map(b -> mapper.map(b, BookingFullDTO.class))
                .collect(Collectors.toList());
    }
}
