/**
 *
 */
package tool.dat.tooldat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * @author balabala1
 *
 */
@Data
@Component
public class CheckListSheet2 implements Iterable<Points>{

	@Autowired
	private Points Point1;
	@Autowired
	private Points Point2;
	@Autowired
	private Points Point3;
	@Autowired
	private Points Point4;
	@Autowired
	private Points Point5;
	@Autowired
	private Points Point6;
	@Autowired
	private Points Point7;
	@Autowired
	private Points Point8;
	@Autowired
	private Points Point9;
	@Autowired
	private Points Point10;
	@Autowired
	private Points Point11;
	@Autowired
	private Points Point12;
	@Autowired
	private Points Point13;
	@Autowired
	private Points Point14;
	@Autowired
	private Points Point15;
	@Autowired
	private Points Point16;

	@Override
	public Iterator<Points> iterator() {
		// TODO Auto-generated method stub
		List<Points> pointIterator = new ArrayList<Points>();
		pointIterator.add(Point1);
		pointIterator.add(Point2);
		pointIterator.add(Point3);
		pointIterator.add(Point4);
		pointIterator.add(Point5);
		pointIterator.add(Point6);
		pointIterator.add(Point7);
		pointIterator.add(Point8);
		pointIterator.add(Point9);
		pointIterator.add(Point10);
		pointIterator.add(Point11);
		pointIterator.add(Point12);
		pointIterator.add(Point13);
		pointIterator.add(Point14);
		pointIterator.add(Point15);
		pointIterator.add(Point16);
		return pointIterator.iterator();
	}
}
