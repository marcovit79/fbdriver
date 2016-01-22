package it.cineca.facebook.crawler;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.TableNotFoundException;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;

import com.google.common.io.Files;

public class PutPageIntoHBase {

	private static final String CRAWLED_DATA_COLUMN_FAMILY = "crawled";
	private static final String HBASE_DEST_TABLE = "prove_marco_facebook";

	public static void main(String[] args) throws InterruptedException, IOException {
		String page = "TetraPak";
		File pagePostsDir = new File(DownloadPages.CROWLED_DIR + page);
		
		Configuration config = HBaseConfiguration.create();
		try(HBaseAdmin admin = new HBaseAdmin(config)) {
			
			HTableDescriptor tableDescriptor;
			try {
				tableDescriptor = admin.getTableDescriptor(TableName.valueOf(HBASE_DEST_TABLE));
			}
			catch(TableNotFoundException exc) {
				tableDescriptor = new HTableDescriptor(TableName.valueOf(HBASE_DEST_TABLE));
			    tableDescriptor.addFamily(new HColumnDescriptor(CRAWLED_DATA_COLUMN_FAMILY));
			    admin.createTable(tableDescriptor);
			    tableDescriptor = admin.getTableDescriptor(TableName.valueOf(HBASE_DEST_TABLE));
			}
			
			
			HTable table = new HTable(config, HBASE_DEST_TABLE);
			byte[] crowledColumnFamily = Bytes.toBytes(CRAWLED_DATA_COLUMN_FAMILY);
			
			for(File f: pagePostsDir.listFiles()) {
				
				String content = String.join("\n", Files.readLines(f, StandardCharsets.UTF_8));
				
				Put postBody = new Put(Bytes.toBytes(f.getName()));
				postBody.add(crowledColumnFamily, Bytes.toBytes("page"), Bytes.toBytes(page));
				postBody.add(crowledColumnFamily, Bytes.toBytes("postBody"), Bytes.toBytes(content));
				table.put(postBody);
			}
			table.flushCommits();
		    table.close();
		};
	}

	
}
